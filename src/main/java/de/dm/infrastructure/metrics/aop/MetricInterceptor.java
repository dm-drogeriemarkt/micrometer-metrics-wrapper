package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.MetricUtils;
import de.dm.infrastructure.metrics.annotation.aop.Metric;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

public class MetricInterceptor implements MethodInterceptor, Ordered {

    public static final String METRIC_COUNTER_PREFIX = "counter";
    public static final String METRIC_COUNTER_SUFFIX = "count";
    public static final String METRIC_GAUGE_PREFIX = "gauge";
    public static final String METRIC_GAUGE_SUFFIX = "executionTime";
    public static final String METRIC_ERROR_COUNTER_SUFFIX = "errorCount";
    private MeterRegistry meterRegistry;

    public MetricInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Exception {

        Method method = invocation.getMethod();
        Metric mergedMetricAnnotation = MetricUtils.getMergedMetricAnnotation(method, invocation.getThis());
        String metricBaseName = mergedMetricAnnotation.name();

        meterRegistry.counter(METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX).increment();
        Timer timer = meterRegistry.timer(METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX);

        final Object o = timer.recordCallable(() -> {
            Object invocationResult = null;
            try {
                invocationResult = invocation.proceed();
            } catch (Throwable throwable) {
                meterRegistry.counter(METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX).increment();
                invocationResult = throwable;
            }

            return invocationResult;
        });

        //TODO find a better way?!
        if (o instanceof Throwable) {
            throw (Throwable) o;
        } else {
            return o;
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
