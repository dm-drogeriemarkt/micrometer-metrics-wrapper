package de.dm.infrastructure.metrics.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

abstract class AbstractAnnotationAdvisor extends AbstractPointcutAdvisor implements InitializingBean {

    Pointcut pointcut;
    Advice advice;

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    /**
     * Calculate a pointcut for the given annotation types, if any.
     *
     * @param annotationType the annotation type to introspect
     * @return the applicable Pointcut object, or {@code null} if none
     */
    Pointcut buildPointcut(Class<? extends Annotation> annotationType) {
        Pointcut cpc = new AnnotationMatchingPointcut(annotationType, true);
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(annotationType);
        return new ComposablePointcut(cpc).union(mpc);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(advice, "Advice is required");
        Assert.notNull(pointcut, "Pointcut is required");
    }
}
