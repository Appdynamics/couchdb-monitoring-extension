package com.appdynamics.monitors.couchdb;

import com.google.gson.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
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
    private static final String SUM_VALUE = "sum";
    private HostConfig hostConfig;

    public CouchDBWrapper(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
    }

    public HashMap gatherMetrics() throws Exception{
        HttpURLConnection connection = null;
        InputStream is = null;
        String cacheServerUrl = constructURL();
        try {
            URL u = new URL(cacheServerUrl);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
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
            logger.error("Invalid URL used to connect to CoucheDB: " + cacheServerUrl);
            throw e;
        } catch(JsonSyntaxException e) {
            logger.error("Error parsing the Json response");
            throw e;
        }
        finally {
            try {
                is.close();
                connection.disconnect();
            } catch (NullPointerException npe) {
                throw npe;
            } catch (Exception e) {
                throw e;
            }
        }
    }


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

                if (!metricValuesObject.get(SUM_VALUE).isJsonNull()) {
                    Number metricValue = metricValuesObject.get(SUM_VALUE).getAsNumber();
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