package de.dm.infrastructure.metrics.aop;


import de.dm.infrastructure.metrics.annotation.aop.Metric;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class MetricUtils {

    private static final Map<AnnotationCacheKey, Metric> findAnnotationCache = new ConcurrentReferenceHashMap<>(256);

    /**
     * Given a {@link MethodInvocation} for a method or class annotated with {@link Metric @Metric}, this method constructs a
     * merged representation of the method and class level annotations. Especially the {@link Metric#name()} will be merged.
     * Resolved annotation will be cached, the merge step only happens the first time an annotation is looked up.
     *
     * @param method The method annotated with {@link Metric @Metric}.
     * @param target The target object annotated with {@link Metric @Metric}.
     * @return An instance of {@link Metric} with method and class attributes merged.
     */
    public static Metric getMergedMetricAnnotation(Method method, Object target) {
        AnnotationCacheKey cacheKey = new AnnotationCacheKey(method, Metric.class);
        return findAnnotationCache.computeIfAbsent(cacheKey, annotationCacheKey -> synthesizeAnnotation(method, target));
    }

    private static Metric synthesizeAnnotation(Method method, Object target) {
        Map<String, Object> attributeMap = new HashMap<>();

        Optional<Metric> methodAnnotation = Optional.ofNullable(AnnotationUtils.findAnnotation(method, Metric.class));
        Optional<Metric> classAnnotation = Optional.ofNullable(AnnotationUtils.findAnnotation(target.getClass(), Metric.class));

        String classKey = classAnnotation
                .map(Metric::name)
                .filter(StringUtils::hasText)
                .orElse(getClassName(method));
        String methodKey = methodAnnotation
                .map(Metric::name)
                .filter(StringUtils::hasText)
                .orElse(method.getName());

        String metricName = new StringBuilder()
                .append(wrapName(StringUtils.uncapitalize(classKey)))
                .append(StringUtils.uncapitalize(methodKey))
                .toString();
        attributeMap.put("name", metricName);
        return AnnotationUtils.synthesizeAnnotation(attributeMap, Metric.class, method);
    }

    static String wrapName(String name) {
        if (StringUtils.hasText(name) && !name.endsWith(".")) {
            name += ".";
        }
        return name;
    }

    public static String getClassName(Method method) {
        return method.getDeclaringClass().getSimpleName();
    }

    /**
     * Copy of {@link org.springframework.core.annotation.AnnotationUtils.AnnotationCacheKey}
     */
    private static class AnnotationCacheKey {

        private final AnnotatedElement element;

        private final Class<? extends Annotation> annotationType;

        public AnnotationCacheKey(AnnotatedElement element, Class<? extends Annotation> annotationType) {
            this.element = element;
            this.annotationType = annotationType;
        }

        @Override
        public int hashCode() {
            return (this.element.hashCode() * 29 + this.annotationType.hashCode());
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof AnnotationCacheKey)) {
                return false;
            }
            AnnotationCacheKey otherKey = (AnnotationCacheKey) other;
            return (this.element.equals(otherKey.element) && this.annotationType.equals(otherKey.annotationType));
        }

    }
}
