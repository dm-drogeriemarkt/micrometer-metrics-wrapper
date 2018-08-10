package de.dm.infrastructure.metrics.annotation.aop;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Metric {

    /**
     * Alias for {@link #name}.
     * <p>This attribute may <strong>not</strong> be used in conjunction with
     * {@link #name}, but it may be used <em>instead</em> of {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Name that will be used when this metric is logged.
     * <p>If used at the class level, all of the class's metrics will be prefixed with this name.
     * If left empty (the default) the class's simple name (as returned by {@link Class#getSimpleName()}) will be used.
     * <p>If used at the method level, metrics will use this name as a suffix.
     * If left empty (the default) the method's name (as returned by {@link Method#getName()}) will be used.
     * <p>This attribute may <strong>not</strong> be used in conjunction with
     * {@link #value}, but it may be used <em>instead</em> of {@link #value}.
     */
    @AliasFor("value")
    String name() default "";
}
