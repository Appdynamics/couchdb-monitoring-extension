package com.appdynamics.extensions.couchdb;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.http.HttpClientUtils;
import com.appdynamics.extensions.http.UrlBuilder;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: Vishaka Sekar on 2019-09-16
 */
 class CouchDBMonitorTask implements AMonitorTaskRunnable {
     private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(CouchDBMonitorTask.class);
     private MetricWriteHelper metricWriteHelper;
     private MonitorContextConfiguration configuration;
     private Map<String, ?> server;
     Phaser phaser;

     CouchDBMonitorTask(MetricWriteHelper metricWriteHelper, MonitorContextConfiguration configuration, Map<String,?> server) {
         this.configuration = configuration;
         this.metricWriteHelper = metricWriteHelper;
         this.server = server;
         phaser = new Phaser();
         phaser.register();
    }

    @Override
    public void run() {
        Map<String, ?> config = configuration.getConfigYml();
        AtomicInteger heartBeat = getConnectionStatus((Map<String, String>) server);
        metricWriteHelper.printMetric(configuration.getMetricPrefix() + "|" + server.get(Constants.DISPLAY_NAME).toString() + "|" + "Connection Status", String.valueOf(heartBeat.get()), "AVERAGE", "AVERAGE", "INDIVIDUAL");
        if (heartBeat.get() == 1) {
            LOGGER.info("Connected to {}", server.get(Constants.DISPLAY_NAME).toString() );
            List<String> nodes = (List<String>) config.get("nodes");
            if(nodes == null || nodes.isEmpty()){
                fetchMetricsFromAllNodes();
            }
            else{
                fetchMetrics(nodes);
            }
        }else{
            LOGGER.info("Extension cannot connect to {}", server.get(Constants.DISPLAY_NAME).toString() );
        }
        phaser.arriveAndAwaitAdvance();
    }

    private void fetchMetrics(List<String> nodes) {
    }

    private void fetchMetricsFromAllNodes() {
        try {
           for(JsonNode clusterNode : getAllClusterNodes()){
               NodeMetricsCollectorTask nodeMetricsCollectorTask = new NodeMetricsCollectorTask(configuration, metricWriteHelper, server.get(Constants.URI).toString(),clusterNode.getTextValue(), phaser);
               configuration.getContext().getExecutorService().submit("Node Task", nodeMetricsCollectorTask);
           }
        } catch (Exception e) {
            LOGGER.info("no nodes in cluster {}", server.get(Constants.DISPLAY_NAME).toString(), e);
        }
    }

    private JsonNode getAllClusterNodes() throws Exception {
        ObjectNode allNodes = HttpClientUtils.getResponseAsJson(configuration.getContext().getHttpClient(), server.get("uri").toString()+"/_membership", ObjectNode.class);
        if(allNodes != null && allNodes.size() > 0){
            return allNodes.get("cluster_nodes");
        }
        else{
            throw new Exception("no nodes available");
        }
    }

    private AtomicInteger getConnectionStatus (Map<String, String> server) {
        String url = getConnectionUrl(server);
        AtomicInteger heartbeat = new AtomicInteger(0);
        String response = HttpClientUtils.getResponseAsStr(this.configuration.getContext().getHttpClient(), url);
        if(response != null){
            heartbeat.set(1);
        }
        return heartbeat;
    }

    private String getConnectionUrl (Map<String, String> server) {
        UrlBuilder urlBuilder = new UrlBuilder(server);
        return urlBuilder.build();
    }

    @Override
    public void onTaskComplete() {
         LOGGER.info("All Tasks completed for server {}", server.get(Constants.DISPLAY_NAME).toString());
    }
}
