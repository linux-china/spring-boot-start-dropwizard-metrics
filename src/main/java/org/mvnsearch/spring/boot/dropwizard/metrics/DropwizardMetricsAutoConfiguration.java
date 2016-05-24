package org.mvnsearch.spring.boot.dropwizard.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * dropwizard metrics auto configuration
 *
 * @author linux_china
 */
@Configuration
public class DropwizardMetricsAutoConfiguration implements ApplicationContextAware {
    private Logger log = LoggerFactory.getLogger(DropwizardMetricsAutoConfiguration.class);
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private MetricRegistry metrics;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object obj = applicationContext.getBean(beanName);
               /*
                * As you are using AOP check for AOP proxying. If you are proxying with Spring CGLIB (not via Spring AOP)
                * Use org.springframework.cglib.proxy.Proxy#isProxyClass to detect proxy If you are proxying using JDK
                * Proxy use java.lang.reflect.Proxy#isProxyClass
                */
            Class<?> objClz = obj.getClass();
            Object targetObj = getTargetObject(obj);
            if (org.springframework.aop.support.AopUtils.isAopProxy(obj)) {
                objClz = org.springframework.aop.support.AopUtils.getTargetClass(obj);
            }
            //metrics gauge
            for (Method m : objClz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Gauge.class)) {
                    Gauge gaugeAnnotation = m.getAnnotation(Gauge.class);
                    String metricName = getMetricName(objClz, gaugeAnnotation.name(), m.getName(), gaugeAnnotation.absolute());
                    metrics.register(metricName, (com.codahale.metrics.Gauge<Number>) () -> {
                        try {
                            return (Number) m.invoke(targetObj);
                        } catch (Exception e) {
                            log.error("Metric-Gauge:" + m.getName(), e);
                        }
                        return null;
                    });
                }
            }
            //metrics histogram
            for (Field field : objClz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Metric.class)) {
                    Metric metricAnnotation = field.getAnnotation(Metric.class);
                    if (ClassUtils.isAssignable(com.codahale.metrics.Metric.class, field.getType())) {
                        try {
                            field.setAccessible(true);
                            if (field.get(targetObj) != null) {
                                com.codahale.metrics.Metric metric = (com.codahale.metrics.Metric) field.get(targetObj);
                                String metricName = getMetricName(objClz, metricAnnotation.name(), field.getName(), metricAnnotation.absolute());
                                metrics.register(metricName, metric);
                            } else {
                                field.set(targetObj, metrics.histogram(metricAnnotation.name()));
                            }
                        } catch (Exception e) {
                            log.error("Metric-Histogram:" + field.getName(), e);
                        }
                    }
                }
            }
        }
    }

    /**
     * get metric name
     *
     * @param clazz       current class
     * @param metricName  metric name in annotation
     * @param elementName element name: field or method name
     * @param absolute    absolute
     * @return metric name
     */
    private String getMetricName(Class clazz, String metricName, String elementName, boolean absolute) {
        String absoluteName = metricName.isEmpty() ? elementName : metricName;
        if (absolute) {
            return absoluteName;
        } else {
            return MetricRegistry.name(clazz.getName(), absoluteName);
        }
    }

    protected <T> T getTargetObject(Object proxy) {
        if (AopUtils.isAopProxy(proxy)) {
            try {
                return (T) ((Advised) proxy).getTargetSource().getTarget();
            } catch (Exception ignore) {

            }
        }
        return (T) proxy;
    }

    @Bean
    public DropwizardMetricsAspect dropwizardMetricsAspect() {
        return new DropwizardMetricsAspect();
    }

    @Bean
    public DropwizardMetricsEndpoint dropwizardMetricsEndpoint() {
        return new DropwizardMetricsEndpoint();
    }
}
