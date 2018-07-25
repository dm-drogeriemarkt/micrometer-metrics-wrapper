package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.testfixtures.*;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.step.StepTimer;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static de.dm.infrastructure.metrics.aop.MetricInterceptor.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetricAnnotationAdvisorTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SimpleMeterRegistry meterRegistry;
    private WithMetricClassAnnotation metricClassAnnotation;
    private WithoutAnnotation withoutAnnotation;
    private InterfaceWithMetricAnnotation interfaceWithMetricAnnotation;
    private InterfaceWithMethodMetricAnnotation interfaceWithMethodMetricAnnotation;

    @Before
    public void setUp() throws Exception {
        MetricAnnotationAdvisor metricAnnotationAdvisor = new MetricAnnotationAdvisor(meterRegistry);
        metricAnnotationAdvisor.afterPropertiesSet();
        this.metricClassAnnotation = SetupUtil.setUpAdvisedClass(WithMetricClassAnnotation.class, metricAnnotationAdvisor);
        this.withoutAnnotation = SetupUtil.setUpAdvisedClass(WithoutAnnotation.class, metricAnnotationAdvisor);
        this.interfaceWithMetricAnnotation = SetupUtil.setUpAdvisedClass(InterfaceWithMetricAnnotationImpl.class, metricAnnotationAdvisor);
        this.interfaceWithMethodMetricAnnotation = SetupUtil.setUpAdvisedClass(InterfaceWithMethodMetricAnnotationImpl.class, metricAnnotationAdvisor);

    }

    @Test
    public void testNoOp() {
        withoutAnnotation.method();

        verifyZeroInteractions(meterRegistry);
    }

    @Test
    public void testCountAndExecutionTimeAreRecorded() {
        Method method = getRealMethod(metricClassAnnotation, "method");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        metricClassAnnotation.method();

        verify(meterRegistry.counter(eq(counterName))).increment();
        verify(meterRegistry).timer(timerName);
    }

    @Test
    public void testInterfaceWorks() {
        Method method = ReflectionUtils.findMethod(InterfaceWithMetricAnnotation.class, "interfaceMethod");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        interfaceWithMetricAnnotation.interfaceMethod();

        verify(meterRegistry.counter(eq(counterName))).increment();
        verify(meterRegistry).timer(timerName);
    }

    @Test
    public void testInterfaceMethodWorks() {
        Method method = ReflectionUtils.findMethod(InterfaceWithMethodMetricAnnotation.class, "annotatedMethod");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;

        interfaceWithMethodMetricAnnotation.annotatedMethod();

        verify(meterRegistry.counter(eq(counterName))).increment();
        verify(meterRegistry).timer(timerName);
    }

    private Method getRealMethod(Object target, String methodName) {
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Method method = ReflectionUtils.findMethod(targetClass, methodName);
        return ReflectionUtils.findMethod(method.getDeclaringClass(), methodName);
    }


}
