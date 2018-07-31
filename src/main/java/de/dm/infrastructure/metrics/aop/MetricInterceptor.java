package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.MetricUtils;
import de.dm.infrastructure.metrics.annotation.aop.Metric;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;
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
    private GenericClassMethodMetrics genericClassMethodMetrics;

    public MetricInterceptor(GenericClassMethodMetrics genericClassMethodMetrics) {
        this.genericClassMethodMetrics = genericClassMethodMetrics;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Metric mergedMetricAnnotation = MetricUtils.getMergedMetricAnnotation(method, invocation.getThis());
        String metricBaseName = mergedMetricAnnotation.name();

        genericClassMethodMetrics.getRegistry().counter(buildCounterName(metricBaseName)).increment();
        Timer timer = genericClassMethodMetrics.getRegistry().timer(buildTimerName(metricBaseName));

        final Object invocationResult = timer.recordCallable(() -> {
            try {
                return invocation.proceed();
            } catch (Throwable throwable) {
                genericClassMethodMetrics.getRegistry().counter(buildErrorCounterName(metricBaseName)).increment();
                return throwable;
            }

        });
        
        if (invocationResult instanceof Throwable) {
            throw (Throwable) invocationResult;
        } else {
            return invocationResult;
        }

    }

    private String buildErrorCounterName(String metricBaseName) {
        return METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_ERROR_COUNTER_SUFFIX;
    }

    private String buildTimerName(String metricBaseName) {
        return METRIC_GAUGE_PREFIX + "." + metricBaseName + "." + METRIC_GAUGE_SUFFIX;
    }

    private String buildCounterName(String metricBaseName) {
        return METRIC_COUNTER_PREFIX + "." + metricBaseName + "." + METRIC_COUNTER_SUFFIX;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
