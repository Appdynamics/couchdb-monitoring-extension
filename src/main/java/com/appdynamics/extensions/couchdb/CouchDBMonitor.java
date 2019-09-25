/** 
 * Copyright 2019 AppDynamics
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
package com.appdynamics.extensions.couchdb;

import com.appdynamics.extensions.ABaseMonitor;
import com.appdynamics.extensions.TasksExecutionServiceProvider;
import com.appdynamics.extensions.couchdb.config.Stats;
import com.appdynamics.extensions.couchdb.util.Constants;
import com.appdynamics.extensions.util.AssertUtils;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CouchDBMonitor extends ABaseMonitor {

    @Override
    protected String getDefaultMetricPrefix() {
        return Constants.DEFAULT_METRIC_PREFIX;
    }

    @Override
    public String getMonitorName() {
        return Constants.COUCHDB_MONITOR;
    }

    @Override
    protected void initializeMoreStuff (Map<String, String> args) {
        this.getContextConfiguration().setMetricXml(args.get("metrics-file"), Stats.class);
    }

    @Override
    protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
        List<Map<String, ?>> servers =  getServers();
        for (Map<String, ?> server : servers) {
            CouchDBMonitorTask task = new CouchDBMonitorTask(tasksExecutionServiceProvider.getMetricWriteHelper(), this.getContextConfiguration(),server);
            AssertUtils.assertNotNull(server.get(Constants.DISPLAY_NAME), "The displayName can not be null");
            tasksExecutionServiceProvider.submit(server.get(Constants.DISPLAY_NAME).toString(), task);
        }
    }

    @Override
    protected List<Map<String, ?>> getServers() {
        return (List<Map<String, ?>>) getContextConfiguration().getConfigYml().get(Constants.SERVERS);
    }

    public static void main(String[] args) throws TaskExecutionException, IOException {

        ConsoleAppender ca = new ConsoleAppender();
        ca.setWriter(new OutputStreamWriter(System.out));
        ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
        ca.setThreshold(Level.DEBUG);
        org.apache.log4j.Logger.getRootLogger().addAppender(ca);


        final CouchDBMonitor monitor = new CouchDBMonitor();
        final Map<String, String> taskArgs = Maps.newHashMap();
        taskArgs.put("config-file", "/Users/vishaka.sekar/AppDynamics/couchdb-monitoring-extension/src/main/resources/conf/config.yml");
        taskArgs.put("metrics-file", "/Users/vishaka.sekar/AppDynamics/couchdb-monitoring-extension/src/main/resources/conf/metrics.xml");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    monitor.execute(taskArgs, null);
                } catch (Exception e) {

                    System.out.println("Error while running the task"+ e);
                }
            }
        }, 2, 60, TimeUnit.SECONDS);

    }
}
