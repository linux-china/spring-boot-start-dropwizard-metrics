package org.mvnsearch.spring.boot.dropwizard.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

import java.util.Map;

/**
 * dropwizard metrics endpoint
 *
 * @author linux_china
 */
public class DropwizardMetricsEndpoint extends AbstractEndpoint<Map<String, Metric>> {
    @Autowired
    private MetricRegistry metrics;

    public DropwizardMetricsEndpoint() {
        super("metrics2");
    }

    public Map<String, Metric> invoke() {
        return metrics.getMetrics();
    }
}
