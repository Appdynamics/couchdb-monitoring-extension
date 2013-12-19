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
package com.appdynamics.monitors.couchdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CouchDBWrapper {

    private static final Logger logger = Logger.getLogger(CouchDBWrapper.class.getSimpleName());
    private static final String CURRENT_VALUE = "current";
    private HostConfig hostConfig;

    public CouchDBWrapper(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    /**
     * Connects to the couchDB host and retrieves metrics using the CouchDB REST API
     * @return 	HashMap     Map containing metrics retrieved from using the CouchDB REST API
     */
    public HashMap gatherMetrics() throws Exception{
        HttpURLConnection connection = null;
        InputStream is = null;
        String cacheServerUrl = constructURL();
        try {
            URL u = new URL(cacheServerUrl);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            logger.info("Connecting to database for host: " + hostConfig.hostId + ":" + hostConfig.port);
            connection.connect();
            is = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                jsonString.append(currentLine);
            }

            JsonObject jsonObject = new JsonParser().parse(jsonString.toString()).getAsJsonObject();
            HashMap hostMetrics = constructMetricsMap(jsonObject);
            return hostMetrics;
        } catch(MalformedURLException e) {
            logger.error("Invalid URL used to connect to CouchDB: " + cacheServerUrl);
            throw e;
        } catch(JsonSyntaxException e) {
            logger.error("Error parsing the Json response");
            throw e;
        } catch(IOException e) {
            throw e;
        }
        finally {
            try {
                if (is != null && connection != null) {
                    is.close();
                    connection.disconnect();
                }
            }catch(Exception e) {
                logger.error("Exception", e);
            }
        }
    }

    /**
     * Constructs a HashMap of metrics based on the JSON Response received upon executing
     * CouchDB's /_stats REST API request
     * @param   metricsObject   JSON Response object
     * @return  HashMap     Map containing the couchDB host metrics
     */
    private HashMap constructMetricsMap(JsonObject metricsObject) throws Exception {
        // 1st level: Metric Category
        // 2nd level: Metric Name
        HashMap<String, HashMap<String, Number>> hostMetrics = new HashMap<String, HashMap<String, Number>>();

        Iterator metricCategoryIterator = metricsObject.entrySet().iterator();

        while (metricCategoryIterator.hasNext()) {
            Map.Entry<String, JsonObject> metricCategoryEntry = (Map.Entry<String, JsonObject>)metricCategoryIterator.next();
            String metricCategory = metricCategoryEntry.getKey();
            JsonObject metricCategoryObject = metricCategoryEntry.getValue();
            Iterator metricNameIterator = metricCategoryObject.entrySet().iterator();

            HashMap<String, Number> metricsNameMap = new HashMap<String,Number>();
            while (metricNameIterator.hasNext()) {
                Map.Entry<String, JsonObject> metricNameEntry = (Map.Entry<String, JsonObject>)metricNameIterator.next();
                String metricName = metricNameEntry.getKey();
                JsonObject metricValuesObject = metricNameEntry.getValue();

                if (!metricValuesObject.get(CURRENT_VALUE).isJsonNull()) {
                    Number metricValue = metricValuesObject.get(CURRENT_VALUE).getAsNumber();
                    metricsNameMap.put(metricName, metricValue);
                }
            }
            hostMetrics.put(metricCategory, metricsNameMap);
        }
        return hostMetrics;
    }

    /**
     * Construct the REST URL for the CoucheDB host
     * @return	The CoucheDB host REST URL string
     */
    private String constructURL() {
        return new StringBuilder()
                .append("http://")
                .append(hostConfig.username)
                .append(":")
                .append(hostConfig.password)
                .append("@")
                .append(hostConfig.hostId)
                .append(":")
                .append(hostConfig.port)
                .append("/_stats")
                .toString();
    }

    public HashMap calculateCurrentMetrics(HashMap oldValues, HashMap newValues) {
        if (oldValues == null || oldValues.isEmpty()) {
            return newValues;
        }

        // Essentially want to subtract. i.e. newValues - oldValues = actual values in the interval
        HashMap<String, HashMap<String, Number>> currentMetrics = new HashMap<String, HashMap<String, Number>>();
        Iterator newValuesIterator = newValues.keySet().iterator();

        while (newValuesIterator.hasNext()) {
            String metricCategory = (String) newValuesIterator.next();
            if (oldValues.containsKey(metricCategory)) {
                HashMap oldMetricMap = (HashMap) oldValues.get(metricCategory);
                HashMap newMetricMap = (HashMap) newValues.get(metricCategory);
                HashMap currentMetricMap = new HashMap<String, Number>();
                Iterator newMetricMapIterator = newMetricMap.keySet().iterator();
                currentMetrics.put(metricCategory, currentMetricMap);

                while (newMetricMapIterator.hasNext()) {
                    String metricName = (String) newMetricMapIterator.next();
                    Double newMetricValue = ((Number) newMetricMap.get(metricName)).doubleValue();
                    if (oldMetricMap.containsKey(metricName)) { // Need to subtract in order to get current values
                        Double oldMetricValue = ((Number) oldMetricMap.get(metricName)).doubleValue();
                        if (newMetricValue - oldMetricValue > 0) {
                            currentMetricMap.put(metricName, newMetricValue - oldMetricValue);
                        }
                        else {
                            currentMetricMap.put(metricName, newMetricValue);
                        }
                    }
                    else { // this is a new metric that is not present in the old metrics map
                        currentMetricMap.put(metricName, newMetricValue);
                    }
                }
            }
        }
        return currentMetrics;
    }
}