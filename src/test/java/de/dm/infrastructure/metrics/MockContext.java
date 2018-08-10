package de.dm.infrastructure.metrics;

import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotation;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotationImpl;
import de.dm.infrastructure.metrics.testfixtures.WithMetricClassAnnotation;
import de.dm.infrastructure.metrics.testfixtures.WithoutAnnotation;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MockContext {

    @Bean
    public WithMetricClassAnnotation withMetricClassAnnotation() {
        return new WithMetricClassAnnotation();
    }

    @Bean
    public WithoutAnnotation withoutAnnotation() {
        return new WithoutAnnotation();
    }

    @Bean
    public InterfaceWithMetricAnnotation interfaceWithMetricAnnotation() {
        return new InterfaceWithMetricAnnotationImpl();
    }

    @Bean
    @ConditionalOnMissingBean(value = SimpleMeterRegistry.class)
    public MeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}
