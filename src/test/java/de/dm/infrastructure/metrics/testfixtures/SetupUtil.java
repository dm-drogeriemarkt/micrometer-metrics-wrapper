package de.dm.infrastructure.metrics.testfixtures;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

public class SetupUtil {


//    public static <T> T setUpClassWithoutAnnotation(Advisor advisor) {
//        ClassWithoutClassAnnotation noProxy = new ClassWithoutClassAnnotation();
//        return createProxy(advisor, noProxy);
//    }

    private static <T> T createProxy(Advisor advisor, Object noProxy) {
        AspectJProxyFactory factory = new AspectJProxyFactory(noProxy);
        factory.addAdvisor(advisor);
        return factory.getProxy();
    }

//    public static <T> T setUpClassAnnotation(Advisor advisor) {
//        ClassWithClassAnnotation noProxy = new ClassWithClassAnnotation();
//        return createProxy(advisor, noProxy);
//    }
//
//    public static <T> T setUpClassWithCustomKey(Advisor advisor) {
//        ClassWithCustomKey noProxy = new ClassWithCustomKey();
//        return createProxy(advisor, noProxy);
//    }
//
//    public static <T> T setUpInterfaceImplementationWithoutAnnotation(Advisor advisor) {
//        ImplementationWithoutAnnotation noProxy = new ImplementationWithoutAnnotation();
//        return createProxy(advisor, noProxy);
//    }
//
//    public static <T> T setUpInterfaceImplementationWithAnnotation(Advisor advisor) {
//        ImplementationWithAnnotation noProxy = new ImplementationWithAnnotation();
//        return createProxy(advisor, noProxy);
//    }

    public static <T> T setUpAdvisedClass(Class<T> clazz, Advisor advisor) throws IllegalAccessException, InstantiationException {
        T noProxy = clazz.newInstance();
        return createProxy(advisor, noProxy);
    }

    public static <T> T setUpMetricClassAnnotation(Advisor advisor) {
        WithMetricClassAnnotation noProxy = new WithMetricClassAnnotation();
        return createProxy(advisor, noProxy);
    }
}
