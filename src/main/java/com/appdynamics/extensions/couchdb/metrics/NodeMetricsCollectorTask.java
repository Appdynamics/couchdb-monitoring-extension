package com.appdynamics.extensions.couchdb.metrics;

import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.couchdb.config.Stat;
import com.appdynamics.extensions.couchdb.config.Stats;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.util.JsonUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Phaser;

/**
 * @author: Vishaka Sekar on 2019-09-16
 */
public class NodeMetricsCollectorTask implements Callable<List<Metric>> {
    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(NodeMetricsCollectorTask.class);
    public static final String STAT_ENDPOINT = "/_stats";
    public static final String NODE_STAT_ENDPOINT = "/_node/";
    private String uri;
    private String nodeName;
    private MonitorContextConfiguration configuration;
    private Phaser phaser;
    private String clusterName;

    public NodeMetricsCollectorTask(MonitorContextConfiguration configuration,
                                    String uri, String clusterName, String nodeName, Phaser phaser) {
        this.uri = uri;
        this.nodeName = nodeName;
        this.configuration = configuration;
        this.clusterName = clusterName;
        this.phaser = phaser;
        this.phaser.register();
    }

    @Override
    public List<Metric> call() {
        List<Metric> metricsList = new ArrayList<>();
        try{
            Stats stats = (Stats) configuration.getMetricsXml();
            Stat[] statArray = stats.getStat();
            JsonNode jsonNode = HttpClientUtils.getResponseAsJson(configuration.getContext().getHttpClient(),
                    uri + NODE_STAT_ENDPOINT + nodeName + STAT_ENDPOINT, JsonNode.class);
            if (jsonNode != null) {
                for (Stat statistic : statArray) {
                    ParseApiResponse parser = new ParseApiResponse(configuration.getMetricPrefix() + "|" +
                            clusterName + "|" + nodeName + "|" + statistic.getType() + "|");
                    metricsList.addAll(parser.extractMetricsFromApiResponse(statistic,
                            JsonUtils.getNestedObject(jsonNode, statistic.getType())));
                }
            }
            phaser.arriveAndDeregister();
        }catch(Exception e ){
            LOGGER.info("Phaser threw exception", e);
        }

        return metricsList;

    }
}
