package de.dm.infrastructure.metrics.annotation.aop;

import de.dm.infrastructure.metrics.testfixtures.InterfaceWithMetricAnnotation;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@DirtiesContext
@RunWith(SpringRunner.class)
public class ApplicationIntegrationTest {
    @Autowired
    private InterfaceWithMetricAnnotation interfaceWithMetricAnnotation;

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Test
    public void testIfMemoryMetricClassIsAttachedToRegistry() {
        this.interfaceWithMetricAnnotation.interfaceMethod();

        AtomicBoolean isIncluded = new AtomicBoolean(false);
        this.compositeMeterRegistry.forEachMeter(meter -> {
            if (meter.getId().getName().equals("jvm.memory.used")) {
                isIncluded.set(true);
            }
        });
        assertTrue(isIncluded.get());
    }
}
