package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.annotation.aop.Metric;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Collections;

public class MetricAnnotationAdvisor extends AbstractAnnotationAdvisor {

    private final MeterRegistry meterRegistry;

    public MetricAnnotationAdvisor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pointcut = buildPointcut(Collections.singleton(Metric.class));
        this.advice = buildAdvice();
        super.afterPropertiesSet();
    }

    private MetricInterceptor buildAdvice() {
        return new MetricInterceptor(this.meterRegistry);
    }
}
