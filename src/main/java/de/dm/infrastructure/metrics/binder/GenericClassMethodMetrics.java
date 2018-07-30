package de.dm.infrastructure.metrics.binder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.NamingConvention;

public class GenericClassMethodMetrics implements MeterBinder {

    private MeterRegistry registry;

    @Override
    public void bindTo(MeterRegistry registry) {

        this.registry = registry;
        this.registry.config().namingConvention(NamingConvention.dot);
    }

    public MeterRegistry getRegistry() {
        return registry;
    }

}
