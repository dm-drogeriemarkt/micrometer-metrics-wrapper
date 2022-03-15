package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotation;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.export.statsd.StatsdProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.micrometer.statsd.StatsdFlavor.TELEGRAF;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
public class ApplicationIntegrationTest {

    @Autowired
    private InterfaceWithMetricAnnotation interfaceWithMetricAnnotation;
    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;
    @Autowired
    private StatsdProperties statsdProperties;

    @Test
    public void testIfMemoryMetricClassIsAttachedToRegistry() {
        this.interfaceWithMetricAnnotation.interfaceMethod();

        AtomicBoolean isIncluded = new AtomicBoolean(false);
        this.compositeMeterRegistry.forEachMeter(meter -> {
            if (meter.getId().getName().equals("jvm.memory.used")) {
                isIncluded.set(true);
            }
        });

        assertThat(isIncluded.get()).isTrue();
    }

    @Test
    public void testThatStatsdFlavorIsTelegraf() {
        assertThat(statsdProperties.getFlavor()).isEqualTo(TELEGRAF);
    }
}
