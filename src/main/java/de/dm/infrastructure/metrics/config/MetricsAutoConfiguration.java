package de.dm.infrastructure.metrics.config;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "metrics", name = "enabled", matchIfMissing = true)
public class MetricsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = MeterRegistry.class)
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public MetricAnnotationAdvisor metricAnnotationAdvisor(MeterRegistry meterRegistry) {
        return new MetricAnnotationAdvisor(meterRegistry);
    }
}
