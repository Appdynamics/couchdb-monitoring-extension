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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.appdynamics.extensions.http.Response;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class CouchDBWrapper {

	private static final Logger logger = Logger.getLogger("com.singularity.extensions.CouchDBWrapper");
	private static final String CURRENT_VALUE = "current";
	private static final String STATS_URI = "/_stats";

	/**
	 * Connects to the couchDB host and retrieves metrics using the CouchDB REST
	 * API
	 * 
	 * @param httpClient
	 * @return HashMap Map containing metrics retrieved from using the CouchDB
	 *         REST API
	 */
	public Map<String, Map<String, Number>> gatherMetrics(SimpleHttpClient httpClient) throws Exception {
		JsonObject statsResponse = getResponse(httpClient, STATS_URI);
		Map<String, Map<String, Number>> hostMetrics = constructMetricsMap(statsResponse);
		return hostMetrics;

	}

	private JsonObject getResponse(SimpleHttpClient httpClient, String uri) {
		String response = getResponseString(httpClient, uri);
		JsonObject jsonObject = null;
		try {
			jsonObject = new JsonParser().parse(response).getAsJsonObject();
		} catch (JsonParseException e) {
			logger.error("Response from " + uri + "is not a json");
		}
		return jsonObject;
	}

	/**
	 * Returns HttpResponse as string from given url
	 * 
	 * @param httpClient
	 * @param path
	 * @return
	 */
	private String getResponseString(SimpleHttpClient httpClient, String path) {
		Response response = null;
		String responseString = "";
		try {
			response = httpClient.target().path(path).get();
			responseString = response.string();
		} catch (Exception e) {
			logger.error("Exception in getting response from " + path, e);
			throw new RuntimeException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception ex) {
				// Ignore
			}
		}
		return responseString;
	}

	/**
	 * Constructs a HashMap of metrics based on the JSON Response received upon
	 * executing CouchDB's /_stats REST API request
	 * 
	 * @param metricsObject
	 *            JSON Response object
	 * @return HashMap Map containing the couchDB host metrics
	 */
	private Map<String, Map<String, Number>> constructMetricsMap(JsonObject metricsObject) throws Exception {
		// 1st level: Metric Category
		// 2nd level: Metric Name
		Map<String, Map<String, Number>> hostMetrics = new HashMap<String, Map<String, Number>>();
		for (Entry<String, JsonElement> group : metricsObject.entrySet()) {
			String couchDBGroup = group.getKey();
			JsonObject subGroup = group.getValue().getAsJsonObject();
			Map<String, Number> metricsNameMap = new HashMap<String, Number>();
			for (Entry<String, JsonElement> entry : subGroup.entrySet()) {
				String metricName = entry.getKey();
				JsonObject jsonObject = entry.getValue().getAsJsonObject();
				if (!jsonObject.get(CURRENT_VALUE).isJsonNull()) {
					Number metricValue = jsonObject.get(CURRENT_VALUE).getAsNumber();
					metricsNameMap.put(metricName, metricValue);
				}
			}
			hostMetrics.put(couchDBGroup, metricsNameMap);
		}
		return hostMetrics;
	}

	public Map<String, Map<String, Number>> calculateCurrentMetrics(Map<String, Map<String, Number>> oldValues,
			Map<String, Map<String, Number>> newMetrics) {
		if (oldValues == null || oldValues.isEmpty()) {
			return newMetrics;
		}
		// Essentially want to subtract. i.e. newValues - oldValues = actual
		// values in the interval
		Map<String, Map<String, Number>> currentMetrics = new HashMap<String, Map<String, Number>>();

		for (Map.Entry<String, Map<String, Number>> categoryMetricEntries : newMetrics.entrySet()) {
			String metricCategory = categoryMetricEntries.getKey();
			Map<String, Number> categoryMetrics = categoryMetricEntries.getValue();
			if (oldValues.containsKey(metricCategory)) {
				Map<String, Number> oldMetricsMap = oldValues.get(metricCategory);
				Map<String, Number> currentMetricMap = new HashMap<String, Number>();

				for (Map.Entry<String, Number> metricEntries : categoryMetrics.entrySet()) {
					String metricName = metricEntries.getKey();
					Double newMetricValue = metricEntries.getValue().doubleValue();
					if (oldMetricsMap.containsKey(metricName)) {
						Double oldMetricValue = oldMetricsMap.get(metricName).doubleValue();
						if (newMetricValue - oldMetricValue > 0) {
							currentMetricMap.put(metricName, newMetricValue - oldMetricValue);
						} else {
							currentMetricMap.put(metricName, newMetricValue);
						}
					} else { // this is a new metric that is not present in the
								// old metrics map
						currentMetricMap.put(metricName, newMetricValue);
					}
				}
				currentMetrics.put(metricCategory, currentMetricMap);
			} else {
				currentMetrics.put(metricCategory, categoryMetrics);
			}
		}
		return currentMetrics;
	}
}
