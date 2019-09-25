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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Phaser;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author: Vishaka Sekar on 2019-09-24
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest({HttpClientUtils.class, org.apache.http.impl.client.CloseableHttpClient.class})

public class ParseApiResponseTest {
    MonitorContextConfiguration configuration;
    MetricWriteHelper metricWriteHelper;
    Phaser phaser  = new Phaser();
    String metricPrefix =  "Custom Metrics|Couch DB";
    ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
    Map<String, ?> conf;
    Logger logger;

    @Before
    public void setUp(){
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
        Mockito.when(configuration.getContext().getExecutorService()).thenReturn(executorService);
        Mockito.doNothing().when(executorService).execute(anyString(), (Runnable) anyObject());
        logger = ExtensionsLoggerFactory.getLogger(CouchDBMonitorTaskTest.class);
        phaser.register();
    }

    @Test
    public void testTopLevelMetricCollection(){
        MonitorContextConfiguration contextConfiguration = new MonitorContextConfiguration("Couch DB", "Custom Metrics|Couch DB|", PathResolver.resolveDirectory(AManagedMonitor.class), Mockito.mock(AMonitorJob.class));
        contextConfiguration.setMetricXml("src/test/resources/metrics.xml", Stats.class);

    }



}
