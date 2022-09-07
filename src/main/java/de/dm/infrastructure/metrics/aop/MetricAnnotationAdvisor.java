package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.annotation.aop.Metric;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;

public class MetricAnnotationAdvisor extends AbstractAnnotationAdvisor {

    private final MetricInterceptor metricInterceptor;

    public MetricAnnotationAdvisor(MetricInterceptor metricInterceptor) {
        this.metricInterceptor = metricInterceptor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.pointcut = buildPointcut(Metric.class);
        this.advice = this.metricInterceptor;
        super.afterPropertiesSet();
    }
}
