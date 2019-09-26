package com.appdynamics.extensions.couchdb.metrics;

import com.appdynamics.extensions.couchdb.config.Stat;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.appdynamics.extensions.util.JsonUtils;
import com.google.common.collect.Lists;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author: Vishaka Sekar on 7/26/19
 */
public class ParseApiResponse {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(ParseApiResponse.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    private String metricPrefix;
    private List<Metric> metricList = Lists.newArrayList();

    public ParseApiResponse(String metricPrefix){
        this.metricPrefix = metricPrefix;
    }

    public List<Metric> extractMetricsFromApiResponse (Stat stat, JsonNode jsonNode) {
         String[] metricPathTokens;
         if (stat.getStats() != null) {
             for (Stat childStat : stat.getStats()) {
                 extractMetricsFromApiResponse(childStat, JsonUtils.getNestedObject(jsonNode, childStat.getType()));
             }
         }
         if (stat.getMetric() != null) {
             for (com.appdynamics.extensions.couchdb.config.Metric metricFromConfig : stat.getMetric()) {
                 JsonNode value = JsonUtils.getNestedObject(jsonNode, metricFromConfig.getAttr());
                 String metricValue;
                 if(value == null){
                    LOGGER.debug("{} not found in response",metricFromConfig.getAttr());
                 }
                 else {
                     if (value.has("value")) {
                         metricValue = value.get("value").toString();
                     } else {
                         metricValue = value.toString();
                     }
                     LOGGER.info("Processing metric [{}] ", metricFromConfig.getAttr());
                     metricPathTokens = metricFromConfig.getAttr().split("\\|");
                     Map<String, String> propertiesMap = objectMapper.convertValue(metricFromConfig, Map.class);
                     Metric metric = new Metric(metricFromConfig.getAttr(), metricValue, propertiesMap, metricPrefix, metricPathTokens);
                     metricList.add(metric);
                 }
             }
         }
        return metricList;
     }
 }

