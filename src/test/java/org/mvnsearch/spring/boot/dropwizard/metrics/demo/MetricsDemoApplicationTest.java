package org.mvnsearch.spring.boot.dropwizard.metrics.demo;

import com.codahale.metrics.MetricRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * metrics demo application test
 *
 * @author linux_china
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MetricsDemoApplication.class)
public class MetricsDemoApplicationTest {
    @Autowired
    private MetricRegistry metrics;
    @Autowired
    private DemoService demoService;

    @Test
    public void testSpike() throws Exception {
        demoService.getName();
        Assert.assertEquals(metrics.getCounters().get("getName").getCount(), 1L);
    }
}
