package de.dm.infrastructure.metrics.testfixtures;


import de.dm.infrastructure.metrics.annotation.aop.Metric;

@Metric
public interface InterfaceWithMetricAnnotation {

    void interfaceMethod();
}
