package de.dm.infrastructure.metrics;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.aop.MetricInterceptor;
import de.dm.infrastructure.metrics.aop.WebFluxMetricInterceptor;
import de.dm.infrastructure.metrics.aop.WebMvcMetricInterceptor;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

@Configuration
@AutoConfigureAfter(CompositeMeterRegistryAutoConfiguration.class)
public class MicrometerMetricsAutoConfiguration {

    @Bean
    public GenericClassMethodMetrics genericClassMethodMetrics() {
        return new GenericClassMethodMetrics();
    }

    @Bean
    @ConditionalOnWebApplication(type = SERVLET)
    public MetricInterceptor webMvcMetricInterceptor(GenericClassMethodMetrics genericClassMethodMetrics) {
        return new WebMvcMetricInterceptor(genericClassMethodMetrics);
    }

    @Bean
    @ConditionalOnWebApplication(type = REACTIVE)
    public MetricInterceptor webFluxMetricInterceptor() {
        return new WebFluxMetricInterceptor();
    }

    @Bean
    public MetricAnnotationAdvisor metricAnnotationAdvisor(MetricInterceptor metricInterceptor) {
        return new MetricAnnotationAdvisor(metricInterceptor);
    }
}
