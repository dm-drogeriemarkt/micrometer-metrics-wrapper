package de.dm.infrastructure.metrics.testfixtures;

import de.dm.infrastructure.metrics.annotation.aop.Metric;

import java.io.IOException;

/**
 * @author Jakob Fels
 */
//tag::usage[]
public class WithMetricMethodAnnotation {

    @Metric
    public void method() {

    }

    public void methodThatThrows() throws IOException {
        throw new IOException("Error");
    }

}
//end::usage[]
