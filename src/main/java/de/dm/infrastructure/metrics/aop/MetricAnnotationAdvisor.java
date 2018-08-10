package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.annotation.aop.Metric;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;

public class MetricAnnotationAdvisor extends AbstractAnnotationAdvisor {

    private final GenericClassMethodMetrics genericClassMethodMetrics;

    public MetricAnnotationAdvisor(GenericClassMethodMetrics genericClassMethodMetrics) {
        this.genericClassMethodMetrics = genericClassMethodMetrics;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pointcut = buildPointcut(Metric.class);
        this.advice = buildAdvice();
        super.afterPropertiesSet();
    }

    private MetricInterceptor buildAdvice() {
        return new MetricInterceptor(this.genericClassMethodMetrics);
    }
}
