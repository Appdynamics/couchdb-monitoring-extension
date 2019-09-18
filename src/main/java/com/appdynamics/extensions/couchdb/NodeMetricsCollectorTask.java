package com.appdynamics.extensions.couchdb;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.couchdb.config.Stat;
import com.appdynamics.extensions.couchdb.config.Stats;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.Phaser;

/**
 * @author: Vishaka Sekar on 2019-09-16
 */
class NodeMetricsCollectorTask implements AMonitorTaskRunnable {
    private String uri;
    private String nodeName;
    private MonitorContextConfiguration configuration;
    private MetricWriteHelper metricWriteHelper;
    private Phaser phaser;
    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(NodeMetricsCollectorTask.class);

    NodeMetricsCollectorTask(MonitorContextConfiguration configuration, MetricWriteHelper metricWriteHelper, String uri, String nodeName, Phaser phaser) {
        this.uri = uri;
        this.nodeName = nodeName;
        this.configuration = configuration;
        this.metricWriteHelper =  metricWriteHelper;
        this.phaser = phaser;
        this.phaser.register();
    }

    @Override
    public void run() {
        Stats stats = (Stats) configuration.getMetricsXml();
        Stat[] statArray = stats.getStat();
        for (Stat statistic : statArray) {
            JsonNode jsonNode = HttpClientUtils.getResponseAsJson(configuration.getContext().getHttpClient(),uri+"/_node/"+nodeName  + statistic.getUrl(), JsonNode.class);
            if(jsonNode != null) {
                ParseApiResponse parser = new ParseApiResponse(jsonNode, configuration.getMetricPrefix() + "|" + nodeName + "|" + statistic.getType() + "|");
                List<Metric> metricsList = parser.extractMetricsFromApiResponse(statistic, jsonNode);
                metricWriteHelper.transformAndPrintMetrics(metricsList);
            }else{
                LOGGER.info("No metrics received from Couch DB for node {}",nodeName);
            }
        }
        phaser.arriveAndDeregister();

    }

    @Override
    public void onTaskComplete() {

    }
}
