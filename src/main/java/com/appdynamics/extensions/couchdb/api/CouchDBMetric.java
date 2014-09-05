package com.appdynamics.extensions.couchdb.api;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.Maps;

public class CouchDBMetric {

	private String metricName;

	private String metricCategory;

	private Map<MetricAggregationType, BigDecimal> values;

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getMetricCategory() {
		return metricCategory;
	}

	public void setMetricCategory(String metricCategory) {
		this.metricCategory = metricCategory;
	}

	public Map<MetricAggregationType, BigDecimal> getValues() {
		if (values == null) {
			values = Maps.newHashMap();
		}
		return values;
	}

	public void setValues(Map<MetricAggregationType, BigDecimal> values) {
		this.values = values;
	}
}
