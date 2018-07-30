package de.dm.infrastructure.metrics.binder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

public class GenericClassMethodMetrics implements MeterBinder {

    private MeterRegistry registry;

    @Override
    public void bindTo(MeterRegistry registry) {

        this.registry = registry;
    }

    public MeterRegistry getRegistry() {
        return registry;
    }

}
