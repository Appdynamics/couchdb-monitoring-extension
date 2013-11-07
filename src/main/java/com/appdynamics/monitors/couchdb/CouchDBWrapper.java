package com.appdynamics.monitors.couchdb;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
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
}