package de.dm.infrastructure.metrics.testfixtures;

import de.dm.infrastructure.metrics.annotation.aop.Metric;

//tag::usage[]
public class WithMetricMethodAnnotation {

    @Metric
    public void method() {

    }

}
//end::usage[]
