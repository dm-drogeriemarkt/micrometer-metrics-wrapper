package de.dm.infrastructure.metrics;

import de.dm.infrastructure.metrics.aop.MetricAnnotationAdvisor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class MicrometerMetricsAutoConfiguration {

    @Bean
    public MetricAnnotationAdvisor metricAnnotationAdvisor(MeterRegistry meterRegistry) {
        return new MetricAnnotationAdvisor(meterRegistry);
    }

    @Bean
    //TODO move additional metrics in an extra config and reference this config
    public JvmMemoryMetrics jvmMemoryMetrics(MeterRegistry meterRegistry) {
        final JvmMemoryMetrics jvmMemoryMetrics = new JvmMemoryMetrics();
        jvmMemoryMetrics.bindTo(meterRegistry);
        return jvmMemoryMetrics;
    }

}
