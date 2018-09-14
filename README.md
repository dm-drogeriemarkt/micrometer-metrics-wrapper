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

## Configuration

The library contains a default [micrometer-registry-statsd](https://github.com/micrometer-metrics/micrometer/tree/master/implementations/micrometer-registry-statsd) which is configured for default datadog communication. 
You can change this by setting the flavor property:

```
management.metrics.export.statsd.flavor
```

The library also contains a set of default metrics which are provided from the [spring-boot-actuator](https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot-actuator)

Further informations about micrometer can be found [here](https://micrometer.io)

## Measurements

For every annotated class/method the library creates the following measurements:

| Measurement                              | Description                                         |
|------------------------------------------|-----------------------------------------------------|
| counter.className.methodName.count       | Will be increased on every method invocation        |
| counter.className.methodName.errorCount  | Will be increased if the method throws an Exception |
| gauge.className.methodName.executionTime | The executionTime of the method                     |

# License

Copyright (c) 2018 dm-drogerie markt GmbH & Co. KG, https://dm.de

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

