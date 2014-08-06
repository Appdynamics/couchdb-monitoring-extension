/** 
 * Copyright 2013 AppDynamics 
 * 
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdynamics.extensions.couchdb;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.appdynamics.extensions.http.SimpleHttpClient;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

public class CouchDBMonitor extends AManagedMonitor {

	private static final String METRICS_SEPARATOR = "|";
	private static final String METRIC_PREFIX = "Custom Metrics|CouchDB|HostId|";
	private Set<HostConfig> hostConfigs;
	private boolean isInitialized = false;
	private Map<String, Map<String, Map<String, Number>>> cachedValues;

	private static final Logger logger = Logger.getLogger("com.singularity.extensions.CouchDBMonitor");

	public CouchDBMonitor() {
		String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
		logger.info(msg);
		System.out.println(msg);
	}

	/**
	 * Initializes the host configurations
	 * 
	 * @param taskArguments
	 *            Map of task arguments.
	 */
	private void initialize(Map<String, String> taskArguments) throws Exception {
		if (!isInitialized) {
			logger.info("Reading hosts' configuration...");
			ConfigurationParser configurationParser = new ConfigurationParser(taskArguments.get("hosts-config-path"));
			hostConfigs = configurationParser.parseHostConfig();
			cachedValues = Maps.newHashMap();
			isInitialized = true;
		}
	}

	/**
	 * Main execution method that uploads the metrics to the AppDynamics
	 * Controller
	 * 
	 * @see com.singularity.ee.agent.systemagent.api.ITask#execute(java.util.Map,
	 *      com.singularity.ee.agent.systemagent.api.TaskExecutionContext)
	 */
	public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
		try {
			initialize(taskArguments);
			logger.info("Executing CouchDBMonitor...");
			for (HostConfig hostConfig : hostConfigs) {
				String host = hostConfig.hostId;
				Map<String, String> httpClientArguments = buildHttpClientArguments(hostConfig);
				SimpleHttpClient httpClient = SimpleHttpClient.builder(httpClientArguments).build();

				CouchDBWrapper couchDBWrapper = new CouchDBWrapper();
				Map<String, Map<String, Number>> metrics = couchDBWrapper.gatherMetrics(httpClient);
				Map<String, Map<String, Number>> hostCachedMetrics = cachedValues.get(host);
				Map<String, Map<String, Number>> currentMetrics = couchDBWrapper.calculateCurrentMetrics(hostCachedMetrics, metrics);
				printMetrics(host, currentMetrics);
				cachedValues.put(host, metrics);
				logger.info("Gathered and Printed metrics successfully for " + host);
			}
			return new TaskOutput("Task successful...");
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		return new TaskOutput("Task failed with errors");
	}

	private Map<String, String> buildHttpClientArguments(HostConfig hostConfig) {
		Map<String, String> taskArguments = Maps.newHashMap();
		taskArguments.put("host", hostConfig.hostId);
		taskArguments.put("port", hostConfig.port);
		taskArguments.put("username", hostConfig.username);
		taskArguments.put("password", hostConfig.password);

		return taskArguments;
	}

	/**
	 * Writes the couchDB metrics to the controller
	 * 
	 * @param hostId
	 *            Name of the CouchDB host
	 * @param metricsMap
	 *            HashMap containing all the couchDB metrics
	 */
	private void printMetrics(String hostId, Map<String, Map<String, Number>> metricsMap) throws Exception {
		for (Map.Entry<String, Map<String, Number>> entry : metricsMap.entrySet()) {
			String metricCategory = entry.getKey();
			Map<String, Number> metricCategoryStats = entry.getValue();
			for (Map.Entry<String, Number> metric : metricCategoryStats.entrySet()) {
				String metricName = metric.getKey();
				Number metricValue = metric.getValue();
				printMetric(hostId + METRICS_SEPARATOR + metricCategory + METRICS_SEPARATOR + metricName, metricValue,
						MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION, MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE,
						MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);
			}
		}
	}

	/**
	 * Returns the metric to the AppDynamics Controller.
	 * 
	 * @param metricName
	 *            Name of the Metric
	 * @param metricValue
	 *            Value of the Metric
	 * @param aggregation
	 *            Average OR Observation OR Sum
	 * @param timeRollup
	 *            Average OR Current OR Sum
	 * @param cluster
	 *            Collective OR Individual
	 */
	private void printMetric(String metricName, Number metricValue, String aggregation, String timeRollup, String cluster) throws Exception {
		if (metricValue != null) {
			MetricWriter metricWriter = super.getMetricWriter(METRIC_PREFIX + metricName, aggregation, timeRollup, cluster);
			String value = String.valueOf((long) metricValue.doubleValue());
			metricWriter.printMetric(value);
			logger.debug("METRIC_PREFIX + metricName : " + value);
		}
	}

	private static String getImplementationVersion() {
		return CouchDBMonitor.class.getPackage().getImplementationTitle();
	}
}
