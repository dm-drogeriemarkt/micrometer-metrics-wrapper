package de.dm.infrastructure.metrics.annotation.aop;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static de.dm.infrastructure.metrics.annotation.aop.MetricType.GAUGE;


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
     * <p>Supports SpEL expressions enclosed in {@code #{}}.<br>
     * The SpEL expression evaluates again a dedicated context that provides the
     * following meta-data:
     * <ul>
     * <li>{@code #result} for a reference to the result of the method invocation.</li>
     * <li>{@code #root.method} and {@code #root.target} for a
     * reference to the {@link Method method} and target object respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be access via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     * <p>This attribute may <strong>not</strong> be used in conjunction with
     * {@link #value}, but it may be used <em>instead</em> of {@link #value}.
     */
    @AliasFor("value")
    String name() default "";

    /**
     * Prefix to be used for this metric.
     * * <p>Supports SpEL expressions enclosed in {@code #{}}.<br>
     * The SpEL expression evaluates against a dedicated context that provides the
     * following meta-data:
     * <ul>
     * <li>{@code #result} for a reference to the result of the method invocation.</li>
     * <li>{@code #root.method} and {@code #root.target} for a
     * reference to the {@link Method method} and target object respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be access via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     */
    String prefix() default "";

    /**
     * <p>A SpEL expression to record a custom metric, enclosed in {@code #{}}. Defaults to {@code ""},
     * which means no additional metric will be collected.<br>
     * The SpEL expression evaluates against a dedicated context that provides the
     * following meta-data:
     * <ul>
     * <li>{@code #result} for a reference to the result of the method invocation.</li>
     * <li>{@code #root.method} and {@code #root.target} for a
     * reference to the {@link Method method} and target object respectively.</li>
     * <li>Shortcuts for the method name ({@code #root.methodName}) and target class
     * ({@code #root.targetClass}) are also available.
     * <li>Method arguments can be accessed by index. For instance the second argument
     * can be access via {@code #root.args[1]}, {@code #p1} or {@code #a1}. Arguments
     * can also be accessed by name if that information is available.</li>
     * </ul>
     */
    String expression() default "";

    /**
     * The type of the metric collected via {@link #expression()}.
     */
    MetricType type() default GAUGE;

    /**
     * The fieldName of the metric collected via {@link #expression()}.
     */
    String fieldName() default "value";

}
