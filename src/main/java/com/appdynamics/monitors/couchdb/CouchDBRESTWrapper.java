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
import java.util.Map;

public class CouchDBRESTWrapper {

    private static final Logger logger = Logger.getLogger(CouchDBRESTWrapper.class.getSimpleName());

    private String host;
    private String port;
    private String username;

    private String password;


    public CouchDBRESTWrapper(Map<String,String> taskArguments) {
        host = taskArguments.get("host");
        port = taskArguments.get("port");
        username = taskArguments.get("username");
        password = taskArguments.get("password");
    }

    public HashMap gatherMetrics() throws Exception{
        HttpURLConnection connection = null;
        InputStream is = null;
        HashMap metrics = new HashMap();
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

            System.out.println("done");



            //metrics = convertResponseToMap(is);
            return metrics;
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

    /**
     * Construct the REST URL for the CoucheDB host
     * @return	The CoucheDB host REST URL string
     */
    private String constructURL() {
        return new StringBuilder()
                .append("http://")
                .append(username)
                .append(":")
                .append(password)
                .append("@")
                .append(host)
                .append(":")
                .append(port)
                .append("/_stats")
                .toString();
    }
}