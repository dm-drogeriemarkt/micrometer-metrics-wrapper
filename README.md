# Metrics
> [Micrometer](https://micrometer.io) wrapper to allow generic class/method level metric values

The Metrics project is a [Micrometer](https://micrometer.io) wrapper to allow generic class/method level metric values. 
For this reason the library offers the ```@Metric``` annotation, which uses ```className.methodName``` as name for measurements. 
For example, if you apply ```@Metric``` to some ```class```
you will get the following measurements for every ```public``` method of the ```class```:

    - counter.className.methodName.count
    - counter.className.methodName.errorCount
    - gauge.className.methodName.executionTime
    
## Runtime Dependencies

    - Java >= 1.8
    - Spring Boot >= 2.0.3-RELEASE
    
The library was compiled against:

    - Spring Boot Acutator 2.0.3-RELEASE

## Installation
```
<dependency>
    <groupId>de.dm.infrastructure</groupId>
    <artifactId>metrics</artifactId>
    <version>1.0.3</version>
</dependency>
```

The library contains all required dependencies, including spring-boot-starter-actuator v2.0.3-RELEASE.

## Usage

As class level annotation

```
@Metric
public class WithMetricClassAnnotation {

    public void method() {
    }

    public void methodThatThrows() throws IOException {
        throw new IOException("Error");
    }
}
```

When used at the class level, metrics for all public methods of the annotated class are collected. 
For example the above snippet leads to the following measurements:

```
    - counter.WithMetricClassAnnotation.method.count
    - counter.WithMetricClassAnnotation.method.errorCount
    - gauge.WithMetricClassAnnotation.method.executionTime
    
    - counter.WithMetricClassAnnotation.methodThatThrows.count
    - counter.WithMetricClassAnnotation.methodThatThrows.errorCount
    - gauge.WithMetricClassAnnotation.methodThatThrows.executionTime
``` 

As method level annotation

```
public class WithMetricMethodAnnotation {

    @Metric
    public void method() {
    }
}
```

When used at the method level, metrics are collected for annotated methods only.

## Measurements

For every annotated class/method the library creates the following measurements:

| Measurement                              | Description                                         |
|------------------------------------------|-----------------------------------------------------|
| counter.className.methodName.count       | Will be increased on every method invocation        |
| counter.className.methodName.errorCount  | Will be increased if the method throws an Exception |
| gauge.className.methodName.executionTime | The executionTime of the method                     |
