/*
 * Copyright 2014. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.couchdb.api;

public enum MetricAggregationType {

	CURRENT("current"), SUM("sum"), MEAN("mean"), STD_DEV("stddev"), MIN("min"), MAX("max");

	private String aggregationType;

	private MetricAggregationType(String name) {
		this.aggregationType = name;
	}

	public String getAggregationType() {
		return aggregationType;
	}

}
