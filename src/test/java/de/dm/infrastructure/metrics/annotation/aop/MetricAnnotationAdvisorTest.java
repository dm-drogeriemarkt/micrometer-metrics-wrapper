package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;
import de.dm.infrastructure.metrics.testfixtures.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.reflect.Method;

import static de.dm.infrastructure.metrics.aop.MetricInterceptor.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class MetricAnnotationAdvisorTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GenericClassMethodMetrics genericClassMethodMetrics;
    private WithMetricClassAnnotation metricClassAnnotation;
    private WithMetricMethodAnnotation metricMethodAnnotation;
    private WithoutAnnotation withoutAnnotation;
    private InterfaceWithMetricAnnotation interfaceWithMetricAnnotation;
    private InterfaceWithMethodMetricAnnotation interfaceWithMethodMetricAnnotation;

    @Before
    public void setUp() throws Exception {
        MetricAnnotationAdvisor metricAnnotationAdvisor = new MetricAnnotationAdvisor(genericClassMethodMetrics);
        metricAnnotationAdvisor.afterPropertiesSet();
        this.metricClassAnnotation = SetupUtil.setUpAdvisedClass(WithMetricClassAnnotation.class, metricAnnotationAdvisor);
        this.metricMethodAnnotation = SetupUtil.setUpAdvisedClass(WithMetricMethodAnnotation.class, metricAnnotationAdvisor);
        this.withoutAnnotation = SetupUtil.setUpAdvisedClass(WithoutAnnotation.class, metricAnnotationAdvisor);
        this.interfaceWithMetricAnnotation = SetupUtil.setUpAdvisedClass(InterfaceWithMetricAnnotationImpl.class, metricAnnotationAdvisor);
        this.interfaceWithMethodMetricAnnotation = SetupUtil.setUpAdvisedClass(InterfaceWithMethodMetricAnnotationImpl.class, metricAnnotationAdvisor);

    }

    @Test
    public void testNoOp() {
        withoutAnnotation.method();

        verifyZeroInteractions(genericClassMethodMetrics);
    }

    @Test
    public void testCountAndExecutionTimeAreRecorded() {
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
    public void testInterfaceWorks() {
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
    public void testInterfaceMethodWorks() {
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
