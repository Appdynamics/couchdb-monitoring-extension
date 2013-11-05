package com.appdynamics.monitors.couchdb;

import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CoucheDBMonitor extends AManagedMonitor{

    //TODO
        // Connect to database
        // Retrieve metrics
        // Print metrics

    private static final String METRIC_PREFIX = "Custom Metrics|CoucheDB|HostId|";
    private static final String HOST_PARAM = "host";
    private static final String PORT_PARAM = "port";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private HashSet<HostConfig> hostConfigs = new HashSet<HostConfig>();
    private boolean isInitialized = false;

    private static final Logger logger = Logger.getLogger(CoucheDBMonitor.class.getSimpleName());

    public static void main(String[] args) throws Exception{
        logger.setLevel(Level.DEBUG);
        CoucheDBMonitor coucheDBMonitor = new CoucheDBMonitor();
        ConfigurationParser configurationParser = new ConfigurationParser("conf/HostConfig.xml");
        HashSet set = configurationParser.parseHostConfig();
        //Map<String,String> taskArguments = new HashMap<String, String>();
        //coucheDBMonitor.execute(taskArguments, null);
    }
    private void initialize(Map<String,String> taskArguments) throws Exception {
        if (!isInitialized) {
            ConfigurationParser configurationParser = new ConfigurationParser(taskArguments.get("hosts-config-path"));
            hostConfigs = configurationParser.parseHostConfig();
            isInitialized = true;
        }
    }



    /**
     * Main execution method that uploads the metrics to the AppDynamics Controller
     * @see com.singularity.ee.agent.systemagent.api.ITask#execute(java.util.Map, com.singularity.ee.agent.systemagent.api.TaskExecutionContext)
     */
    public TaskOutput execute(Map<String, String> taskArguments, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        logger.info("Exceuting CoucheDBMonitor...");
        try {
            initialize(taskArguments);

            for (HostConfig hostConfig : hostConfigs) {
                CouchDBRESTWrapper couchDBRESTWrapper = new CouchDBRESTWrapper(hostConfig);
                HashMap metrics = couchDBRESTWrapper.gatherMetrics();
                logger.info("Gathered metrics successfully. Size of metrics: " + metrics.size());
                //printMetrics(metrics);
                logger.info("Printed metrics successfully");
            }
            return new TaskOutput("Task successful...");

        } catch(MalformedURLException e) {
            logger.error("Check the url for the host", e);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
        return new TaskOutput("Task failed with errors");
    }

}
