/*
 * Copyright 2014. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

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
