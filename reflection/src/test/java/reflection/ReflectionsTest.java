package reflection;

import annotation.Controller;
import annotation.Repository;
import annotation.Service;
import java.lang.annotation.Annotation;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReflectionsTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsTest.class);

    @Test
    void showAnnotationClass() throws Exception {
        Reflections reflections = new Reflections("examples");
        List<Class<? extends Annotation>> annotations = List.of(Controller.class, Service.class, Repository.class);

        for (Class<? extends Annotation> annotation : annotations) {
            log.info("annotation => " + annotation +
                    " annotatedTypes => " + reflections.getTypesAnnotatedWith(annotation));
        }
    }
}
