package org.mvnsearch.spring.boot.dropwizard.metrics.demo;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.Gauge;
import com.codahale.metrics.annotation.Metric;
import org.springframework.stereotype.Component;

/**
 * demo application test
 *
 * @author linux_china
 */
@Component
public class DemoServiceImpl implements DemoService {
    @Metric(name = "histogram-1")
    private Histogram histogram;

    @Counted(name = "getName")
    public String getName() {
        histogram.update(1);
        return "jacky";
    }

    @Gauge(name = "rate-1",absolute = true)
    public Double rate() {
        return 26.5;
    }

}
