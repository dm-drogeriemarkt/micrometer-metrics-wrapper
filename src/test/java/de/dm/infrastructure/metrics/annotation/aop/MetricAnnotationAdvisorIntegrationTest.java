package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.aop.MetricInterceptor;
import de.dm.infrastructure.metrics.testfixtures.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.Ordered;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static de.dm.infrastructure.metrics.aop.MetricInterceptor.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@RunWith(SpringRunner.class)
public class MetricAnnotationAdvisorIntegrationTest {

    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private WithMetricClassAnnotation withMetricClassAnnotation;
    @Autowired
    private WithoutAnnotation withoutAnnotation;
    @Autowired
    private InterfaceWithMetricAnnotation interfaceWithMetricAnnotation;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private MetricAnnotationAdvisor metricAnnotationAdvisor;

    @Test
    public void testNoOp() throws Exception {
        withoutAnnotation.method();

        Method method = getRealMethod(withMetricClassAnnotation, "method");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;

        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(0.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isEqualTo(0.0);
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isEqualTo(0.0);
    }

    @Test
    public void testCountAndExecutionTimeAreRecorded() {
        withMetricClassAnnotation.method();

        Method method = getRealMethod(withMetricClassAnnotation, "method");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;

        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isEqualTo(0.0);
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotEqualTo(0.0);
    }

    @Test
    public void testInterfaceWorks() {
        interfaceWithMetricAnnotation.interfaceMethod();

        Method method = ReflectionUtils.findMethod(InterfaceWithMetricAnnotationImpl.class, "interfaceMethod");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;


        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isEqualTo(0.0);
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotEqualTo(0.0);
    }

    @Test
    public void testRepositoryWorks() {
        testRepository.findAll();

        Method method = ReflectionUtils.findMethod(TestRepository.class, "findAll");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;


        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isEqualTo(0.0);
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotEqualTo(0.0);
    }

    @Test
    public void testWithMetricMethodError() {
        try {
            withMetricClassAnnotation.methodThatThrows();
        } catch (IOException e) {
            Method method = getRealMethod(withMetricClassAnnotation, "methodThatThrows");
            String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
            String metricBaseName = className + "." + method.getName();
            String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
            String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
            String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;

            assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
            assertThat(meterRegistry.counter(errorCounterName).count()).isEqualTo(1.0);
            assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotEqualTo(0.0);
        }
    }

    @Test
    public void testMetricInterceptorHasHighestPriority() {
        final MetricInterceptor metricInterceptor = (MetricInterceptor) metricAnnotationAdvisor.getAdvice();
        assertThat(metricInterceptor.getOrder() == Ordered.HIGHEST_PRECEDENCE);
    }

    private Method getRealMethod(Object target, String methodName) {
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Method method = ReflectionUtils.findMethod(targetClass, methodName);
        return ReflectionUtils.findMethod(method.getDeclaringClass(), methodName);
    }
}
