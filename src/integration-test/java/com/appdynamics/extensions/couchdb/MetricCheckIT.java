package com.appdynamics.extensions.couchdb;

import com.appdynamics.extensions.controller.apiservices.CustomDashboardAPIService;
import com.appdynamics.extensions.controller.apiservices.MetricAPIService;
import com.appdynamics.extensions.util.JsonUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.appdynamics.extensions.util.JsonUtils.getTextValue;

/**
 * @author: {Vishaka Sekar} on {10/3/19}
 */
public class MetricCheckIT {
    private MetricAPIService metricAPIService;
    private CustomDashboardAPIService customDashboardAPIService;

    @Before
    public void setup() {
        metricAPIService = IntegrationTestUtils.initializeMetricAPIService();
        customDashboardAPIService = IntegrationTestUtils.initializeCustomDashboardAPIService();
    }

    @Test
    public void whenInstanceIsUpThenHeartBeatIs1ForServer() {
        JsonNode jsonNode = null;
        if (metricAPIService != null) {
            jsonNode = metricAPIService.getMetricData("",
                    "Server%20&%20Infrastructure%20Monitoring/metric-data?metric-path=Application%20Infrastructure%20Performance%7CRoot%7CCustom%20Metrics%7CCouch%20DB%7Ccluster1%7CConnection%20Status&time-range-type=BEFORE_NOW&duration-in-mins=15&output=JSON");
        }
        Assert.assertNotNull("Cannot connect to controller API", jsonNode);
        if (jsonNode != null) {
            JsonNode valueNode = JsonUtils.getNestedObject(jsonNode, "*", "metricValues", "*", "value");
            int heartBeat = (valueNode == null) ? 0 : valueNode.get(0).asInt();
            Assert.assertEquals("heartbeat is 0", heartBeat, 1);
        }
    }

    @Test
    public void checkTotalNumberOfMetricsReportedIsGreaterThan1() {
        JsonNode jsonNode = null;
        if (metricAPIService != null) {
            jsonNode = metricAPIService.getMetricData("",
                    "Server%20&%20Infrastructure%20Monitoring/metric-data?metric-path=Application%20Infrastructure%20Performance%7CRoot%7CCustom%20Metrics%7CCouch%20DB%7CMetrics%20Uploaded&time-range-type=BEFORE_NOW&duration-in-mins=15&output=JSON");
        }
        Assert.assertNotNull("Cannot connect to controller API", jsonNode);
        if (jsonNode != null) {
            JsonNode valueNode = JsonUtils.getNestedObject(jsonNode, "*", "metricValues", "*", "value");
            int totalNumberOfMetricsReported = (valueNode == null) ? 0 : valueNode.get(0).asInt();
            Assert.assertTrue(totalNumberOfMetricsReported > 1);
        }
    }

    @Test
    public void checkDashboardsUploaded() {
        if (customDashboardAPIService != null) {
            JsonNode allDashboardsNode = customDashboardAPIService.getAllDashboards();
            boolean dashboardPresent = isDashboardPresent("CouchDB Dashboard", allDashboardsNode);
            Assert.assertTrue(dashboardPresent);
        }
    }

    private boolean isDashboardPresent(String dashboardName, JsonNode existingDashboards) {
        if (existingDashboards != null) {
            for (JsonNode existingDashboard : existingDashboards) {
                if (dashboardName.equals(getTextValue(existingDashboard.get("name")))) {
                    return true;
                }
            }
        }
        return false;
    }

}

