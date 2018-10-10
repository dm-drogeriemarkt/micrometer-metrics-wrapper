package de.dm.infrastructure.metrics;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@AutoConfigureAfter(CompositeMeterRegistryAutoConfiguration.class)
public class MicrometerMetricsAutoConfiguration {

    @Bean
    public GenericClassMethodMetrics genericClassMethodMetrics() {
        return new GenericClassMethodMetrics();
    }

    @Bean
    public MetricAnnotationAdvisor metricAnnotationAdvisor(GenericClassMethodMetrics genericClassMethodMetrics) {
        return new MetricAnnotationAdvisor(genericClassMethodMetrics);
    }
}
