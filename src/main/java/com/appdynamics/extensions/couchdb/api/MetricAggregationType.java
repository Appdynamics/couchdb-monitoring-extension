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
