package reflection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit4Test> clazz = Junit4Test.class;

        Junit4Test instance = clazz.getDeclaredConstructor().newInstance();

        List<Method> myTestMethods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(MyTest.class))
                .collect(Collectors.toList());
        for (Method method : myTestMethods) {
            method.invoke(instance);
        }
    }
}
