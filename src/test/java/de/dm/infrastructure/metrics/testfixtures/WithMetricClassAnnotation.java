package de.dm.infrastructure.metrics.testfixtures;

import de.dm.infrastructure.metrics.annotation.aop.Metric;

import java.io.IOException;

@Metric
public class WithMetricClassAnnotation {

    public void method() {

    }

    public void methodThatThrows() throws IOException {
        throw new IOException("Error");
    }

}
