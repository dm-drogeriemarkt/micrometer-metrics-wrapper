package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.annotation.aop.Metric;
import io.micrometer.core.instrument.Timer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

public interface MetricInterceptor extends MethodInterceptor, Ordered {

}
