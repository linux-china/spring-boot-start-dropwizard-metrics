package org.mvnsearch.spring.boot.dropwizard.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * metrics aspect
 *
 * @author linux_china
 */
@Aspect
public class DropwizardMetricsAspect {
    @Autowired
    private MetricRegistry metrics;

    @After("execution(* *(..)) && @annotation(metered)")
    public void metered(Metered metered) throws Throwable {
        metrics.meter(metered.name()).mark();
    }

    @AfterThrowing("execution(* *(..)) && @annotation(exceptionMetered)")
    public void exceptionMetered(ExceptionMetered exceptionMetered) throws Throwable {
        metrics.meter(ExceptionMetered.DEFAULT_NAME_SUFFIX + "." + exceptionMetered.name()).mark();
    }

    @After("execution(* *(..)) && @annotation(counted)")
    public void counted(Counted counted) throws Throwable {
        metrics.counter(counted.name()).inc();
    }

    @Around("execution(* *(..)) && @annotation(timed)")
    public Object timed(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        Timer.Context context = metrics.timer(timed.name()).time();
        try {
            return pjp.proceed();
        } finally {
            context.stop();
        }
    }

}
