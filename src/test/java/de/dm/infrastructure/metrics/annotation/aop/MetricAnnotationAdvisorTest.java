package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMethodMetricAnnotation;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMethodMetricAnnotationImpl;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotation;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotationImpl;
import de.dm.infrastructure.metrics.testfixtures.SetupUtil;
import de.dm.infrastructure.metrics.testfixtures.WithMetricClassAnnotation;
import de.dm.infrastructure.metrics.testfixtures.WithMetricMethodAnnotation;
import de.dm.infrastructure.metrics.testfixtures.WithoutAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.reflect.Method;

import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_COUNTER_PREFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_COUNTER_SUFFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_GAUGE_PREFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_GAUGE_SUFFIX;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MetricAnnotationAdvisorTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GenericClassMethodMetrics genericClassMethodMetrics;
    private WithMetricClassAnnotation metricClassAnnotation;
    private WithMetricMethodAnnotation metricMethodAnnotation;
    private WithoutAnnotation withoutAnnotation;
    private InterfaceWithMetricAnnotation interfaceWithMetricAnnotation;
    private InterfaceWithMethodMetricAnnotation interfaceWithMethodMetricAnnotation;

    @BeforeEach
    void setUp() throws Exception {
        MetricAnnotationAdvisor metricAnnotationAdvisor = new MetricAnnotationAdvisor(genericClassMethodMetrics);
        metricAnnotationAdvisor.afterPropertiesSet();
        this.metricClassAnnotation = SetupUtil.setUpAdvisedClass(WithMetricClassAnnotation.class, metricAnnotationAdvisor);
        this.metricMethodAnnotation = SetupUtil.setUpAdvisedClass(WithMetricMethodAnnotation.class, metricAnnotationAdvisor);
        this.withoutAnnotation = SetupUtil.setUpAdvisedClass(WithoutAnnotation.class, metricAnnotationAdvisor);
        this.interfaceWithMetricAnnotation = SetupUtil.setUpAdvisedClass(InterfaceWithMetricAnnotationImpl.class, metricAnnotationAdvisor);
        this.interfaceWithMethodMetricAnnotation = SetupUtil.setUpAdvisedClass(InterfaceWithMethodMetricAnnotationImpl.class, metricAnnotationAdvisor);
    }

    @Test
    void testNoOp() {
        withoutAnnotation.method();

        Mockito.verifyNoInteractions(genericClassMethodMetrics);
    }

    @Test
    void testCountAndExecutionTimeAreRecorded() {
        Method method = getRealMethod(metricClassAnnotation, "method");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        metricClassAnnotation.method();

        verify(genericClassMethodMetrics.getRegistry().counter(eq(counterName))).increment();
        verify(genericClassMethodMetrics.getRegistry()).timer(timerName);

        method = getRealMethod(metricMethodAnnotation, "method");
        className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        metricBaseName = className + "." + method.getName();
        counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        metricMethodAnnotation.method();

        verify(genericClassMethodMetrics.getRegistry().counter(eq(counterName))).increment();
        verify(genericClassMethodMetrics.getRegistry()).timer(timerName);
    }

    @Test
    void testInterfaceWorks() {
        Method method = ReflectionUtils.findMethod(InterfaceWithMetricAnnotation.class, "interfaceMethod");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        interfaceWithMetricAnnotation.interfaceMethod();

        verify(genericClassMethodMetrics.getRegistry().counter(eq(counterName))).increment();
        verify(genericClassMethodMetrics.getRegistry()).timer(timerName);
    }

    @Test
    void testInterfaceMethodWorks() {
        Method method = ReflectionUtils.findMethod(InterfaceWithMethodMetricAnnotation.class, "annotatedMethod");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        interfaceWithMethodMetricAnnotation.annotatedMethod();

        verify(genericClassMethodMetrics.getRegistry().counter(eq(counterName))).increment();
        verify(genericClassMethodMetrics.getRegistry()).timer(timerName);
    }

    private Method getRealMethod(Object target, String methodName) {
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Method method = ReflectionUtils.findMethod(targetClass, methodName);
        return ReflectionUtils.findMethod(method.getDeclaringClass(), methodName);
    }
}
