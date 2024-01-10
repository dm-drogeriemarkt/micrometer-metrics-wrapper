package de.dm.infrastructure.metrics.binder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.NamingConvention;
import org.springframework.lang.NonNull;

/**
 * Generic Metrics that only binds a {@link MeterRegistry} that we can use later
 * to create {@link io.micrometer.core.instrument.Counter} and {@link io.micrometer.core.instrument.Timer}.
 */
public class GenericClassMethodMetrics implements MeterBinder {

    private MeterRegistry registry;

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {

        this.registry = registry;
        this.registry.config().namingConvention(NamingConvention.dot);
    }

    public MeterRegistry getRegistry() {
        return registry;
    }
}
