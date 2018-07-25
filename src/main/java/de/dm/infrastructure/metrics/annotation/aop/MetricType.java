package de.dm.infrastructure.metrics.annotation.aop;

/**
 * @author Jakob Fels
 */
public enum MetricType {

    GAUGE,
    COUNTER;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
