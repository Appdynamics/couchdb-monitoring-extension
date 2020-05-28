/**
 * Copyright 2019 AppDynamics
 * <p>
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

import java.util.List;
import java.util.Map;

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
    protected void initializeMoreStuff(Map<String, String> args) {
        this.getContextConfiguration().setMetricXml(args.get("metrics-file"), Stats.class);
    }

    @Override
    protected void doRun(TasksExecutionServiceProvider tasksExecutionServiceProvider) {
        List<Map<String, ?>> servers = getServers();
        for (Map<String, ?> server : servers) {
            AssertUtils.assertNotNull(server.get(Constants.DISPLAY_NAME), "The displayName can not be null");
            CouchDBMonitorTask task = new CouchDBMonitorTask(tasksExecutionServiceProvider.getMetricWriteHelper(),
                    this.getContextConfiguration(), server);
            tasksExecutionServiceProvider.submit(server.get(Constants.DISPLAY_NAME).toString(), task);
        }
    }

    @Override
    protected List<Map<String, ?>> getServers() {
        return (List<Map<String, ?>>) getContextConfiguration().getConfigYml().get(Constants.SERVERS);
    }
}
