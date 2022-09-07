package de.dm.infrastructure.metrics.aop;

import de.dm.infrastructure.metrics.annotation.aop.Metric;
import de.dm.infrastructure.metrics.binder.GenericClassMethodMetrics;
import io.micrometer.core.instrument.Timer;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class WebFluxMetricInterceptor implements MetricInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        Metric mergedMetricAnnotation = MetricUtils.getMergedMetricAnnotation(method, invocation.getThis());
        String metricBaseName = mergedMetricAnnotation.name();

        Object result = invocation.proceed();

        if (result instanceof Flux<?> flux) {
            return flux.name(metricBaseName).metrics();
        } else  if (result instanceof Mono<?> mono) {
            return mono.name(metricBaseName).metrics();
        }

        return result;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
