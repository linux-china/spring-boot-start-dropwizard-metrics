spring-boot-start-dropwizard-metrics
====================================
Spring boot starter for DropWizard Metrics Annotation。

[![Build Status](https://travis-ci.org/linux-china/spring-boot-start-dropwizard-metrics.svg?branch=master)](https://travis-ci.org/linux-china/spring-boot-start-dropwizard-metrics)

### 如何使用

* 在Spring Boot项目的pom.xml中添加以下依赖:
```xml
          <dependency>
                     <groupId>com.mvnsearch.spring.boot</groupId>
                     <artifactId>spring-boot-starter-dropwizard-metrics</artifactId>
                     <version>1.0.0-SNAPSHOT</version>
          </dependency>
```         
* 接下来在你的代码中直接使用对应的Mapper就可以啦: 

```
            @Metric(name = "histogram-1")
            private Histogram histogram;
            
            @Counted(name = "echo")
            public void echo() {
                ...
            }
```

### 参考文档

* Metrics Javadoc: http://metrics.dropwizard.io/3.1.0/apidocs/
