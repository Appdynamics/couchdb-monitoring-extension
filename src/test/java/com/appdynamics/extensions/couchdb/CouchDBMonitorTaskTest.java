package com.appdynamics.extensions.couchdb;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.AMonitorJob;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContext;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.couchdb.config.Stats;
import com.appdynamics.extensions.executorservice.MonitorExecutorService;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

import static com.appdynamics.extensions.http.HttpClientUtils.getResponseAsStr;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author: Vishaka Sekar on 2019-09-23
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest({HttpClientUtils.class, org.apache.http.impl.client.CloseableHttpClient.class})
public class CouchDBMonitorTaskTest {

    MonitorContextConfiguration configuration;
    MetricWriteHelper metricWriteHelper;
    Phaser phaser = new Phaser();
    String metricPrefix = "Custom Metrics|Couch DB";
    ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
    Map<String, ?> conf;
    Logger logger;


    @Before
    public void setUp() {
        conf = YmlReader.readFromFileAsMap(new File("src/test/resources/config.yml"));
        configuration = mock(MonitorContextConfiguration.class);
        metricWriteHelper = mock(MetricWriteHelper.class);
        ABaseMonitor baseMonitor = mock(ABaseMonitor.class);
        MonitorContext context = mock(MonitorContext.class);
        Mockito.when(baseMonitor.getContextConfiguration()).thenReturn(configuration);
        Mockito.when(baseMonitor.getContextConfiguration().getContext()).thenReturn(context);
        MetricPathUtils.registerMetricCharSequenceReplacer(baseMonitor);
        MetricCharSequenceReplacer replacer = MetricCharSequenceReplacer.createInstance(conf);
        Mockito.when(context.getMetricCharSequenceReplacer()).thenReturn(replacer);
        Mockito.when(configuration.getMetricPrefix()).thenReturn(metricPrefix);
        MonitorExecutorService executorService = mock(MonitorExecutorService.class);
        when(configuration.getContext().getExecutorService()).thenReturn(executorService);
        Mockito.doNothing().when(executorService).execute(anyString(), anyObject());
        logger = ExtensionsLoggerFactory.getLogger(CouchDBMonitorTaskTest.class);
        phaser.register();

    }

    @Test
    public void whenServerUpThenHeartBeatReturns1() {

        MonitorContextConfiguration contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        contextConfiguration.setConfigYml("src/test/resources/config.yml");
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        List<Map<String, ?>> servers = (List<Map<String, ?>>) contextConfiguration.getConfigYml().get("servers");
        for (Map<String, ?> server : servers) {
            CouchDBMonitorTask task = new CouchDBMonitorTask(metricWriteHelper, contextConfiguration, server);
            PowerMockito.mockStatic(HttpClientUtils.class);
            PowerMockito.mockStatic(CloseableHttpClient.class);
            PowerMockito.when(getResponseAsStr(any(CloseableHttpClient.class), anyString())).thenReturn("");
            task.run();
        }
        verify(metricWriteHelper, times(2)).printMetric(pathCaptor.capture(), pathCaptor.capture(), anyString(), anyString(), anyString());
        List objectMetricList = pathCaptor.getAllValues();
        Assert.assertTrue(objectMetricList.get(0).toString().equals("Custom Metrics|Couch DB|myCluster|Connection Status"));
        Assert.assertTrue(objectMetricList.get(1).toString().equals("1"));
    }

    @Test
    public void whenServerNotUpThenHeartBeatReturns0() {

        MonitorContextConfiguration contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        contextConfiguration.setConfigYml("src/test/resources/config.yml");
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        List<Map<String, ?>> servers = (List<Map<String, ?>>) contextConfiguration.getConfigYml().get("servers");
        for (Map<String, ?> server : servers) {
            CouchDBMonitorTask task = new CouchDBMonitorTask(metricWriteHelper, contextConfiguration, server);
            PowerMockito.mockStatic(HttpClientUtils.class);
            PowerMockito.mockStatic(CloseableHttpClient.class);
            PowerMockito.when(getResponseAsStr(any(CloseableHttpClient.class), anyString())).thenReturn(null);
            task.run();
        }
        verify(metricWriteHelper, times(2)).printMetric(pathCaptor.capture(), pathCaptor.capture(), anyString(), anyString(), anyString());
        List objectMetricList = pathCaptor.getAllValues();
        Assert.assertTrue(objectMetricList.get(0).toString().equals("Custom Metrics|Couch DB|myCluster|Connection Status"));
        Assert.assertTrue(objectMetricList.get(1).toString().equals("0"));
    }

    @Test
    public void whenNoNodeFilterSpecifiedThenShouldNotCollectMetrics() {
        MonitorContextConfiguration contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        contextConfiguration.setConfigYml("src/test/resources/config_without_node_filter.yml");
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        List<Map<String, ?>> servers = (List<Map<String, ?>>) contextConfiguration.getConfigYml().get("servers");
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.mockStatic(CloseableHttpClient.class);
        PowerMockito.when(getResponseAsStr(any(CloseableHttpClient.class), anyString())).thenReturn("200 ok");
        when(HttpClientUtils.getResponseAsJson(any(CloseableHttpClient.class), anyString(), any(Class.class))).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ObjectMapper mapper = new ObjectMapper();
                        String url = (String) invocationOnMock.getArguments()[1];
                        File file = null;
                        if (url.contains("_node")) {
                            file = new File("src/test/resources/stats_api_response.json");
                        }
                        if (url.contains("_membership")) {
                            file = new File("src/test/resources/multinode_membership_response.json");
                        }
                        JsonNode objectNode = mapper.readValue(file, JsonNode.class);
                        return objectNode;
                    }
                });
        for (Map<String, ?> server : servers) {
            CouchDBMonitorTask task = new CouchDBMonitorTask(metricWriteHelper, contextConfiguration, server);
            task.run();
        }
        verify(metricWriteHelper, times(2)).printMetric(pathCaptor.capture(), pathCaptor.capture(), anyString(), anyString(), anyString());
        System.out.println(pathCaptor.getAllValues()); //todo: assert
    }

    @Test
    public void whenNodeFilterHasValueThenCollectMetrics() throws InterruptedException {
        MonitorContextConfiguration contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        contextConfiguration.setConfigYml("src/test/resources/config_with_node_filter.yml");
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        List<Map<String, ?>> servers = (List<Map<String, ?>>) contextConfiguration.getConfigYml().get("servers");
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.mockStatic(CloseableHttpClient.class);
        PowerMockito.when(getResponseAsStr(any(CloseableHttpClient.class), anyString())).thenReturn("200 ok");
        when(HttpClientUtils.getResponseAsJson(any(CloseableHttpClient.class), anyString(), any(Class.class))).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ObjectMapper mapper = new ObjectMapper();
                        String url = (String) invocationOnMock.getArguments()[1];
                        File file = null;
                        if (url.contains("_node")) {
                            file = new File("src/test/resources/stats_api_response.json");
                        }
                        if (url.contains("_membership")) {
                            file = new File("src/test/resources/multinode_membership_response.json");
                        }
                        JsonNode objectNode = mapper.readValue(file, JsonNode.class);
                        return objectNode;
                    }
                });
        for (Map<String, ?> server : servers) {
            CouchDBMonitorTask task = new CouchDBMonitorTask(metricWriteHelper, contextConfiguration, server);
            task.run();
        }
        ArgumentCaptor<List> pathCaptorList = ArgumentCaptor.forClass(List.class);
        Thread.sleep(100);
        verify(metricWriteHelper, times(2)).transformAndPrintMetrics(pathCaptorList.capture());
        System.out.println(pathCaptorList.getAllValues()); //todo: assert
    }

    @Test
    public void whenNodeFilterHasEmptyStringThenShouldNotCollectMetrics() {
        MonitorContextConfiguration contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        contextConfiguration.setConfigYml("src/test/resources/config_with_node_filter_empty_string.yml");
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);
        List<Map<String, ?>> servers = (List<Map<String, ?>>) contextConfiguration.getConfigYml().get("servers");
        PowerMockito.mockStatic(HttpClientUtils.class);
        PowerMockito.mockStatic(CloseableHttpClient.class);
        PowerMockito.when(getResponseAsStr(any(CloseableHttpClient.class), anyString())).thenReturn("200 ok");
        when(HttpClientUtils.getResponseAsJson(any(CloseableHttpClient.class), anyString(), any(Class.class))).thenAnswer(
                new Answer() {
                    public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                        ObjectMapper mapper = new ObjectMapper();
                        String url = (String) invocationOnMock.getArguments()[1];
                        File file = null;
                        if (url.contains("_node")) {
                            file = new File("src/test/resources/stats_api_response.json");
                        }
                        if (url.contains("_membership")) {
                            file = new File("src/test/resources/multinode_membership_response.json");
                        }
                        JsonNode objectNode = mapper.readValue(file, JsonNode.class);
                        return objectNode;
                    }
                });
        for (Map<String, ?> server : servers) {
            CouchDBMonitorTask task = new CouchDBMonitorTask(metricWriteHelper, contextConfiguration, server);
            task.run();
        }
        ArgumentCaptor<List> pathCaptorList = ArgumentCaptor.forClass(List.class);
        verify(metricWriteHelper, times(0)).transformAndPrintMetrics(pathCaptorList.capture());
    }
}
