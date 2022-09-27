package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Class<?>> knownClasses;
    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.knownClasses = new HashSet<>(classes);
        this.beans = new HashSet<>();
        initialize(classes);
    }

    public static DIContainer createContainerForPackage(String rootPackageName) {
        return new DIContainer(ClassPathScanner.getAllClassesInPackage(rootPackageName));
    }

    private void initialize(Set<Class<?>> classes) {
        Set<Object> beans = initializeBeans(classes);
        postProcess(beans);
        this.beans.addAll(beans);
    }

    private Set<Object> initializeBeans(Set<Class<?>> classes) {
        return classes.stream()
                .map(this::tryCreate)
                .collect(Collectors.toSet());
    }

    private Object tryCreate(Class<?> clazz) {
        Class<?> target = findConstructableClass(clazz);
        return Arrays.stream(target.getDeclaredConstructors())
                .map(this::tryConstruct)
                .filter(Objects::nonNull)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("cannot create as knowned classes"));
    }

    private Class<?> findConstructableClass(Class<?> clazz) {
        if (!clazz.isInterface()) {
            return clazz;
        }
        return knownClasses.stream()
                .filter(clazz::isAssignableFrom)
                .findAny()
                .orElseThrow();
    }

    private <T> T tryConstruct(Constructor<T> constructor) {
        Parameter[] parameters = constructor.getParameters();

        if (!isConstructable(parameters)) {
            return null;
        }
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameterClazz = parameters[i].getType();
            arguments[i] = findBeanToInject(parameterClazz);
        }
        return createInstance(constructor, arguments);
    }

    private Object findBeanToInject(Class<?> parameterClazz) {
        Object bean = getBean(parameterClazz);
        if (bean == null) {
            bean = tryCreate(parameterClazz);
        }
        return bean;
    }

    private static <T> T createInstance(Constructor<T> constructor, Object[] arguments) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(bean -> bean.getClass().isAssignableFrom(aClass))
                .findAny()
                .orElse(null);
    }

    private boolean isConstructable(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(Parameter::getType)
                .allMatch(parameter -> this.knownClasses.stream().anyMatch(parameter::isAssignableFrom));
    }

    private void postProcess(Set<Object> beans) {
        for (Object bean : beans) {
            List<Field> fields = getInjectRequiredFields(bean.getClass());
            for (Field field : fields) {
                inject(field, bean, beans);
            }
        }
    }

    private List<Field> getInjectRequiredFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
    }

    private void inject(Field field, Object instance, Set<Object> beans) {
        Object value = findValueToInject(field.getType(), beans);
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object findValueToInject(Class<?> requiredClass, Set<Object> beans) {
        return beans.stream()
                .filter(bean -> requiredClass.isAssignableFrom(bean.getClass()))
                .findAny()
                .orElseThrow();
    }
}
