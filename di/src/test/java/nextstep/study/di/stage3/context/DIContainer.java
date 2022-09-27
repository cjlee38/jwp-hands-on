package nextstep.study.di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
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

    private void initialize(Set<Class<?>> classes) {
        Set<Object> beans = initializeBeans(classes);
        this.beans.addAll(beans);
    }

    private Set<Object> initializeBeans(Set<Class<?>> classes) {
        return classes.stream()
                .map(this::tryCreate)
                .collect(Collectors.toSet());
    }

    private Object tryCreate(Class<?> clazz) {
        Class<?> target = findConstructableClass(clazz);
        return Arrays.stream(target.getConstructors())
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
}
