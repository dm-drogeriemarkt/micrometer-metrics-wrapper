package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.aop.MetricInterceptor;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotation;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotationImpl;
import de.dm.infrastructure.metrics.testfixtures.TestRepository;
import de.dm.infrastructure.metrics.testfixtures.WithMetricClassAnnotation;
import de.dm.infrastructure.metrics.testfixtures.WithoutAnnotation;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_COUNTER_PREFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_COUNTER_SUFFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_ERROR_COUNTER_SUFFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_GAUGE_PREFIX;
import static de.dm.infrastructure.metrics.aop.MetricInterceptor.METRIC_GAUGE_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@SpringBootTest
@DirtiesContext
class MetricAnnotationAdvisorIntegrationTest {

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
    void testNoOp() {
        withoutAnnotation.method();

        Method method = getRealMethod(withMetricClassAnnotation, "method");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;

        assertThat(meterRegistry.counter(counterName).count()).isZero();
        assertThat(meterRegistry.counter(errorCounterName).count()).isZero();
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isZero();
    }

    @Test
    void testCountAndExecutionTimeAreRecorded() {
        withMetricClassAnnotation.method();

        Method method = getRealMethod(withMetricClassAnnotation, "method");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;

        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isZero();
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotZero();
    }

    @Test
    void testInterfaceWorks() {
        interfaceWithMetricAnnotation.interfaceMethod();

        Method method = ReflectionUtils.findMethod(InterfaceWithMetricAnnotationImpl.class, "interfaceMethod");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;


        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isZero();
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotZero();
    }

    @Test
    void testRepositoryWorks() {
        testRepository.findAll();

        Method method = ReflectionUtils.findMethod(TestRepository.class, "findAll");
        String className = Introspector.decapitalize(method.getDeclaringClass().getSimpleName());
        String metricBaseName = className + "." + method.getName();
        String counterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
        String timerName = METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
        String errorCounterName = METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;


        assertThat(meterRegistry.counter(counterName).count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter(errorCounterName).count()).isZero();
        assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotZero();
    }

    @Test
    void testWithMetricMethodError() {
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
            assertThat(meterRegistry.timer(timerName).totalTime(TimeUnit.MILLISECONDS)).isNotZero();
        }
    }

    @Test
    void testMetricInterceptorHasHighestPriority() {
        final MetricInterceptor metricInterceptor = (MetricInterceptor) metricAnnotationAdvisor.getAdvice();
        assertThat(metricInterceptor.getOrder()).isEqualTo(HIGHEST_PRECEDENCE);
    }

    private Method getRealMethod(Object target, String methodName) {
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Method method = ReflectionUtils.findMethod(targetClass, methodName);
        return ReflectionUtils.findMethod(method.getDeclaringClass(), methodName);
    }
}
