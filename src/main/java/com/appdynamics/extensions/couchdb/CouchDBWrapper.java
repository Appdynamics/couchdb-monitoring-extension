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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.appdynamics.extensions.couchdb.api.CouchDBMetric;
import com.appdynamics.extensions.couchdb.api.MetricAggregationType;
import com.appdynamics.extensions.http.Response;
import com.appdynamics.extensions.http.SimpleHttpClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class CouchDBWrapper {

	private static final Logger logger = Logger.getLogger("com.singularity.extensions.CouchDBWrapper");
	private static final String STATS_URI = "/_stats?range=60";

	/**
	 * Connects to the couchDB host and retrieves metrics using the CouchDB REST
	 * API
	 * 
	 * @param httpClient
	 * @return HashMap Map containing metrics retrieved from using the CouchDB
	 *         REST API
	 */
	public List<CouchDBMetric> gatherMetrics(SimpleHttpClient httpClient) throws Exception {
		JsonObject statsResponse = getResponse(httpClient, STATS_URI);
		List<CouchDBMetric> metricsList = constructMetricsList(statsResponse);
		return metricsList;

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
	 * @return
	 */
	public List<CouchDBMetric> constructMetricsList(JsonObject metricsObject) {
		// 1st level: Metric Category
		// 2nd level: Metric Name
		// 3rd level: MetricAggregationType
		List<CouchDBMetric> metricsList = Lists.newArrayList();
		for (Entry<String, JsonElement> category : metricsObject.entrySet()) {
			JsonObject categoryMetrics = category.getValue().getAsJsonObject();
			for (Entry<String, JsonElement> metric : categoryMetrics.entrySet()) {
				CouchDBMetric couchDBMetric = new CouchDBMetric();
				couchDBMetric.setMetricCategory(category.getKey());
				couchDBMetric.setMetricName(metric.getKey());
				JsonObject jsonObject = metric.getValue().getAsJsonObject();
				Map<MetricAggregationType, BigDecimal> values = Maps.newHashMap();
				for (MetricAggregationType type : MetricAggregationType.values()) {
					if (!jsonObject.get(type.getAggregationType()).isJsonNull()) {
						BigDecimal metricValue = jsonObject.get(type.getAggregationType()).getAsBigDecimal();
						logger.debug("Category:" + couchDBMetric.getMetricCategory() + " MetricName:" + couchDBMetric.getMetricName() + " " + type
								+ ":" + metricValue);
						values.put(type, metricValue);
					}
				}
				couchDBMetric.setValues(values);
				metricsList.add(couchDBMetric);
			}
		}
		return metricsList;
	}
}
