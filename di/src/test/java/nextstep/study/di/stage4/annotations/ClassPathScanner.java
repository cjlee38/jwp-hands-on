package nextstep.study.di.stage4.annotations;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;

public class ClassPathScanner {

    private static final List<Class<? extends Annotation>> knownClasses = List.of(Service.class, Repository.class);

    public static Set<Class<?>> getAllClassesInPackage(final String packageName) {
        Reflections reflections = new Reflections(packageName);
        return knownClasses.stream()
                .flatMap(annotation -> reflections.getTypesAnnotatedWith(annotation).stream())
                .collect(Collectors.toSet());
    }
}
