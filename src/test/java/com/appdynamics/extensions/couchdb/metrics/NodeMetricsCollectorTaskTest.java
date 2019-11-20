package com.appdynamics.extensions.couchdb.metrics;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.AMonitorJob;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContext;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.couchdb.CouchDBMonitorTaskTest;
import com.appdynamics.extensions.couchdb.config.Stats;
import com.appdynamics.extensions.couchdb.metrics.NodeMetricsCollectorTask;
import com.appdynamics.extensions.executorservice.MonitorExecutorService;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.metrics.MetricCharSequenceReplacer;
import com.appdynamics.extensions.util.MetricPathUtils;
import com.appdynamics.extensions.util.PathResolver;
import com.appdynamics.extensions.yml.YmlReader;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

import static com.appdynamics.extensions.http.HttpClientUtils.getResponseAsStr;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author: Vishaka Sekar on 2019-09-24
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest({HttpClientUtils.class, org.apache.http.impl.client.CloseableHttpClient.class})
public class NodeMetricsCollectorTaskTest {

    MonitorContextConfiguration contextConfiguration;
    MetricWriteHelper metricWriteHelper;
    Phaser phaser = new Phaser();
    String metricPrefix = "Custom Metrics|Couch DB";
    Map<String, ?> conf;
    Logger logger;
    JsonNode clusterNodes = null;

    @Before
    public void setUp() {
        conf = YmlReader.readFromFileAsMap(new File("src/test/resources/config.yml"));
        MonitorContextConfiguration configuration = mock(MonitorContextConfiguration.class);
        contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        metricWriteHelper = mock(MetricWriteHelper.class);
        ABaseMonitor baseMonitor = mock(ABaseMonitor.class);
        MonitorContext context = mock(MonitorContext.class);
        PowerMockito.when(baseMonitor.getContextConfiguration()).thenReturn(configuration);
        PowerMockito.when(baseMonitor.getContextConfiguration().getContext()).thenReturn(context);
        MetricPathUtils.registerMetricCharSequenceReplacer(baseMonitor);
        MetricCharSequenceReplacer replacer = MetricCharSequenceReplacer.createInstance(conf);
        PowerMockito.when(context.getMetricCharSequenceReplacer()).thenReturn(replacer);
        Mockito.when(configuration.getMetricPrefix()).thenReturn(metricPrefix);
        MonitorExecutorService executorService = mock(MonitorExecutorService.class);
        when(configuration.getContext().getExecutorService()).thenReturn(executorService);
        Mockito.doNothing().when(executorService).execute(anyString(), anyObject());
        logger = ExtensionsLoggerFactory.getLogger(CouchDBMonitorTaskTest.class);
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.mockStatic(CloseableHttpClient.class);
        PowerMockito.when(getResponseAsStr(any(CloseableHttpClient.class), anyString())).thenReturn("");
        when(HttpClientUtils.getResponseAsJson(any(CloseableHttpClient.class), anyString(), any(Class.class))).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ObjectMapper mapper = new ObjectMapper();
                        String url = (String) invocationOnMock.getArguments()[1];
                        File file = null;
                        if (url.contains("_node")) {
                            file = new File("src/test/resources/stats_api_response.json");
                        }
                        JsonNode objectNode = mapper.readValue(file, JsonNode.class);
                        return objectNode;
                    }
                });
        File file = new File("src/test/resources/multinode_membership_response.json");
        try {
            ObjectMapper mapper = new ObjectMapper();
            clusterNodes = mapper.readValue(file, JsonNode.class).get("cluster_nodes");
        } catch (IOException ioe) {
            logger.info("cannot open mock response for membership api ");
        }
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        phaser.register();
    }

    @Test
    public void collectNodeMetrics() {
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        contextConfiguration.setConfigYml("src/test/resources/config.yml");
        phaser.register();
        NodeMetricsCollectorTask nodeMetricsCollectorTask = new NodeMetricsCollectorTask(contextConfiguration,
                "localhost:5984", "displayName", "couchdb@localhost", phaser);
        List<Metric> metricList = nodeMetricsCollectorTask.call();
        Assert.assertTrue(metricList.get(0).getMetricPath().equalsIgnoreCase("Custom Metrics|Couch DB|displayName|couchdb@localhost|couchdb|auth%cache%hits"));
        Assert.assertTrue(metricList.get(1).getMetricPath().equalsIgnoreCase("Custom Metrics|Couch DB|displayName|couchdb@localhost|couchdb|auth%cache%misses"));

    }
}
