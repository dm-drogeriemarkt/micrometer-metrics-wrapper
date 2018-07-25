package de.dm.infrastructure.metrics;

import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotation;
import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotationImpl;
import de.dm.infrastructure.metrics.testfixtures.WithMetricClassAnnotation;
import de.dm.infrastructure.metrics.testfixtures.WithoutAnnotation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

}
