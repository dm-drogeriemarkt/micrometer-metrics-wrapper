package de.dm.infrastructure.metrics.testfixtures;

import de.dm.infrastructure.metrics.annotation.aop.Metric;

public interface InterfaceWithMethodMetricAnnotation {

    @Metric
    void annotatedMethod();
}
