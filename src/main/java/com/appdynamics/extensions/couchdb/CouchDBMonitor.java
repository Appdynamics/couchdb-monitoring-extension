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

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.appdynamics.TaskInputArgs;
import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.couchdb.api.CouchDBMetric;
import com.appdynamics.extensions.couchdb.api.MetricAggregationType;
import com.appdynamics.extensions.couchdb.config.ConfigUtil;
import com.appdynamics.extensions.couchdb.config.Configuration;
import com.appdynamics.extensions.couchdb.config.Server;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;

public class CouchDBMonitor extends AManagedMonitor {

	private static final Logger logger = Logger.getLogger("com.singularity.extensions.CouchDBMonitor");
	private static final String METRICS_SEPARATOR = "|";
	public static final String CONFIG_ARG = "config-file";
	// To load the config files
	private final static ConfigUtil<Configuration> configUtil = new ConfigUtil<Configuration>();

	public CouchDBMonitor() {
		String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
		logger.info(msg);
		System.out.println(msg);
	}

	/**
	 * Main execution method that uploads the metrics to the AppDynamics
	 * Controller
	 * 
	 * @see com.singularity.ee.agent.systemagent.api.ITask#execute(java.util.Map,
	 *      com.singularity.ee.agent.systemagent.api.TaskExecutionContext)
	 */
	public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
		if (taskArguments != null) {
			logger.info("Starting the CouchDB Monitoring Task");
			if (logger.isDebugEnabled()) {
				logger.debug("Task Arguments Passed ::" + taskArguments);
			}
			// initialize(taskArguments);
			String configFilename = getConfigFilename(taskArguments.get(CONFIG_ARG));
			try {
				// read the config.
				Configuration config = configUtil.readConfig(configFilename, Configuration.class);
				if (config != null && config.getServers() != null) {
					for (Server hostConfig : config.getServers()) {
						Map<String, String> httpClientArguments = buildHttpClientArguments(hostConfig);
						SimpleHttpClient httpClient = SimpleHttpClient.builder(httpClientArguments).build();

						CouchDBWrapper couchDBWrapper = new CouchDBWrapper();
						List<CouchDBMetric> metrics = couchDBWrapper.gatherMetrics(httpClient);
						printMetrics(config, hostConfig, metrics);
						logger.info("Gathered and Printed metrics successfully for " + hostConfig.getDisplayName());
					}
					logger.info("CouchDB Monitoring task completed successfully.");
					return new TaskOutput("CouchDB Monitoring task completed successfully.");
				}
			} catch (FileNotFoundException e) {
				logger.error("Config file not found :: " + configFilename, e);
			} catch (Exception e) {
				logger.error("Metrics collection failed", e);
			}
		}
		logger.info("CouchDB monitoring task completed with failures.");
		throw new TaskExecutionException("CouchDB monitoring task completed with failures.");
	}

	private Map<String, String> buildHttpClientArguments(Server hostConfig) {
		Map<String, String> taskArguments = Maps.newHashMap();
		taskArguments.put(TaskInputArgs.HOST, hostConfig.getHost());
		taskArguments.put(TaskInputArgs.PORT, String.valueOf(hostConfig.getPort()));
		taskArguments.put(TaskInputArgs.USER, hostConfig.getUsername());
		taskArguments.put(TaskInputArgs.PASSWORD, hostConfig.getPassword());

		return taskArguments;
	}

	/**
	 * Writes the couchDB metrics to the controller
	 * 
	 * @param config
	 *            Name of the CouchDB host
	 * @param hostConfig
	 * @param metrics
	 *            HashMap containing all the couchDB metrics
	 */
	private void printMetrics(Configuration config, Server hostConfig, List<CouchDBMetric> metrics) throws Exception {
		String metricPrefix = config.getMetricPrefix();
		for (CouchDBMetric metric : metrics) {
			String metricCategory = metric.getMetricCategory();
			String metricName = metric.getMetricName();
			Map<MetricAggregationType, BigDecimal> metricStats = metric.getValues();
			for (Entry<MetricAggregationType, BigDecimal> typedValues : metricStats.entrySet()) {
				String aggregationType = typedValues.getKey().getAggregationType();
				BigDecimal metricValue = typedValues.getValue();
				printMetric(metricPrefix + hostConfig.getDisplayName() + METRICS_SEPARATOR + metricCategory + METRICS_SEPARATOR + metricName
						+ METRICS_SEPARATOR + aggregationType, metricValue);
			}
		}
	}

	/**
	 * Returns the metric to the AppDynamics Controller.
	 * 
	 * @param metricName
	 * @param metricValue
	 * @throws Exception
	 */
	private void printMetric(String metricName, BigDecimal metricValue) throws Exception {
		if (metricValue != null) {
			MetricWriter metricWriter = super.getMetricWriter(metricName, MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION,
					MetricWriter.METRIC_TIME_ROLLUP_TYPE_AVERAGE, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);
			String value = metricValue.setScale(0, RoundingMode.HALF_UP).toString();
			metricWriter.printMetric(value);
			logger.debug(metricName + " : " + value);
		}
	}

	/**
	 * Returns a config file name,
	 * 
	 * @param filename
	 * @return String
	 */
	private String getConfigFilename(String filename) {
		if (filename == null) {
			return "";
		}
		// for absolute paths
		if (new File(filename).exists()) {
			return filename;
		}
		// for relative paths
		File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
		String configFileName = "";
		if (!Strings.isNullOrEmpty(filename)) {
			configFileName = jarPath + File.separator + filename;
		}
		return configFileName;
	}

	private static String getImplementationVersion() {
		return CouchDBMonitor.class.getPackage().getImplementationTitle();
	}
}
