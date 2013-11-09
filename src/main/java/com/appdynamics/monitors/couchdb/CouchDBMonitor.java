package com.appdynamics.monitors.couchdb;

import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class CouchDBMonitor extends AManagedMonitor{

    private static final String METRIC_PREFIX = "Custom Metrics|CouchDB|HostId|";
    private HashSet<HostConfig> hostConfigs = new HashSet<HostConfig>();
    private boolean isInitialized = false;
    private static HashMap cachedValues = new HashMap();

    private static final Logger logger = Logger.getLogger(CouchDBMonitor.class.getSimpleName());

    public static void main(String[] args) throws Exception{
        logger.setLevel(Level.DEBUG);
        CouchDBMonitor couchDBMonitor = new CouchDBMonitor();
        Map<String,String> taskArguments = new HashMap<String, String>();
        taskArguments.put("hosts-config-path", "conf/HostsConfig.xml");
        couchDBMonitor.execute(taskArguments, null);
    }

    /**
     * Initializes the host configurations
     * @param   taskArguments   Map of task arguments.
     */
    private void initialize(Map<String,String> taskArguments) throws Exception {
        if (!isInitialized) {
            logger.setLevel(Level.INFO);
            logger.info("Reading hosts' configuration...");
            ConfigurationParser configurationParser = new ConfigurationParser(taskArguments.get("hosts-config-path"));
            hostConfigs = configurationParser.parseHostConfig();
            isInitialized = true;
        }
    }

    /**
     * Main execution method that uploads the metrics to the AppDynamics Controller
     * @see com.singularity.ee.agent.systemagent.api.ITask#execute(java.util.Map, com.singularity.ee.agent.systemagent.api.TaskExecutionContext)
     */
    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        try {
            initialize(taskArguments);
            logger.info("Exceuting CouchDBMonitor...");
            for (HostConfig hostConfig : hostConfigs) {
                CouchDBWrapper couchDBWrapper = new CouchDBWrapper(hostConfig);
                HashMap metrics = couchDBWrapper.gatherMetrics();
                HashMap currentMetrics = couchDBWrapper.calculateCurrentMetrics(cachedValues, metrics);
                logger.info("Gathered metrics successfully. Size of metrics: " + metrics.size());
                printMetrics(hostConfig.hostId, currentMetrics);
                cachedValues = metrics;
                logger.info("Printed metrics successfully");
            }
            return new TaskOutput("Task successful...");
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return new TaskOutput("Task failed with errors");
    }
    /**
     * Writes the couchDB metrics to the controller
     * @param   hostId          Name of the CouchDB host
     * @param 	metricsMap		HashMap containing all the couchDB metrics
     */
    private void printMetrics(String hostId, HashMap metricsMap) throws Exception{
        HashMap<String, HashMap<String, Number>> metrics = (HashMap<String,HashMap<String,Number>>) metricsMap;
        Iterator outerIterator = metrics.keySet().iterator();

        while (outerIterator.hasNext()) {
            String metricCategory = outerIterator.next().toString();
            HashMap<String, Number> metricCategoryStats = metrics.get(metricCategory);
            Iterator innerIterator = metricCategoryStats.keySet().iterator();
            while (innerIterator.hasNext()) {
                String metricName = innerIterator.next().toString();
                Number metric = metricCategoryStats.get(metricName);
                printMetric(hostId + "|" + metricCategory + "|" + metricName, metric,
                        MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
                        MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
                        MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);
            }
        }
    }

    /**
     * Returns the metric to the AppDynamics Controller.
     * @param 	metricName		Name of the Metric
     * @param 	metricValue		Value of the Metric
     * @param 	aggregation		Average OR Observation OR Sum
     * @param 	timeRollup		Average OR Current OR Sum
     * @param 	cluster			Collective OR Individual
     */
    private void printMetric(String metricName, Number metricValue, String aggregation, String timeRollup, String cluster) throws Exception
    {
        MetricWriter metricWriter = super.getMetricWriter(METRIC_PREFIX + metricName,
                aggregation,
                timeRollup,
                cluster
        );
        metricWriter.printMetric(String.valueOf((long) metricValue.doubleValue()));
    }
}
