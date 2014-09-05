CouchDB Monitoring Extension
============================
This extension works only with the standalone machine agent.


## Use Case

Apache CouchDB, commonly referred to as CouchDB, is an open source database that focuses on ease of use and on being "a database that completely embraces the web." It is a NoSQL database that uses JSON to store data, uses JavaScript as its query language using MapReduce, and uses HTTP for an API. The CouchDB monitoring extension gathers metrics for the specified hosts that have couchDB installed. 

## Installation ##

1. Run "mvn clean install" and find the CouchDBMonitor.zip file in the "target" folder. You can also download the CouchDBMonitor.zip from [AppDynamics Exchange][http://community.appdynamics.com/t5/eXchange-Community-AppDynamics/CouchDB-Monitoring-Extension/idi-p/5537].
2. Unzip as "CouchDBMonitor" and copy the "CouchDBMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`

## Configuration ##

Note : Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a [yaml validator](http://yamllint.com/)

1. Configure the couchdb instances by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/CouchDBMonitor/`.

   For eg.
   ```
        # List of couchdb instances
        servers:
          - host: "localhost"
            port: 5984
            username: ""
            password: ""
            displayName: "localhost"

         - host: "host"
            port: 5985
            username: ""
            password: ""
            displayName: "host"

        #prefix used to show up metrics in AppDynamics
        metricPrefix:  "Custom Metrics|CouchDB|"

   ```
   
2. Configure the path to the config.yml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/CouchDBMonitor/` directory. Below is the sample

     ```
     <task-arguments>
         <!-- config file-->
         <argument name="config-file" is-required="true" default-value="monitors/CouchDBMonitor/config.yml" />
          ....
     </task-arguments>
    ```

Note : By default, a Machine agent or a AppServer agent can send a fixed number of metrics to the controller. To change this limit, please follow the instructions mentioned [here](http://docs.appdynamics.com/display/PRO14S/Metrics+Limits).
For eg.  
```    
    java -Dappdynamics.agent.maxMetrics=2500 -jar machineagent.jar
```

## Directory Structure

| Directory/File | Description |
|----------------|-------------|
|src/main/resources/conf            | Contains the monitor.xml file and config.yml|
|src/main/java             | Contains source code of the CouchDB monitoring extension |
|target            | Only obtained when using maven. Run 'mvn clean install' to get the distributable .zip file |
|pom.xml       | Maven build script to package the project (required only if changing Java code) |

## Metrics

### Metric Category: couchdb

|Metric Name           | Description     |
|----------------------|-----------------|
|database_writes       | Number of times a database was changed |
|database_reads        | Number of times a document was read from a database |
|open_databases        | Number of open databases |
|open_os_files         | Number of file descriptors CouchDB has open |
|request_time          | Length of a request (ms) inside CouchDB |

### Metric Category: httpd

|Metric Name           | Description     |
|----------------------|-----------------|
|bulk_requests         | Number of bulk requests |
|requests              | Number of HTTP requests |
|temporary_view_reads  | Number of temporary view reads |
|view_reads            | Number of view reads |

### Metric Category: httpd_request_methods

|Metric Name           | Description     |
|----------------------|-----------------|
|COPY       		   | Number of HTTP COPY requests |
|DELETE                | Number of HTTP DELETE requests |
|GET                   | Number of HTTP GET requests |
|HEAD                  | Number of HTTP HEAD requests |
|MOVE                  | Number of HTTP MOVE requests |
|POST                  | Number of HTTP POST requests |
|PUT                   | Number of HTTP PUT requests |

### Metric Category: httpd_status_codes

|Metric Name           | Description     |
|----------------------|-----------------|
|201       			   | Number of HTTP 200 OK responses |
|201        		   | Number of HTTP 201 Created responses |
|202        		   | Number of HTTP 202 Accepted responses |
|301         		   | Number of HTTP 301 Moved Permanently responses |
|304          		   | Number of HTTP 304 Not Modified responses |
|400         		   | Number of HTTP 400 Bad Request responses |
|401                   | Number of HTTP 401 Unauthorized responses|
|403  				   | Number of HTTP 403 Forbidden responses |
|404            	   | Number of HTTP 404 Not Found responses |
|405       		   	   | Number of HTTP 405 Method Not Allowed responses |
|409                   | Number of HTTP 409 Conflict responses |
|412                   | Number of HTTP 412 Precondition Failed responses |
|500                   | Number of  HTTP 500 Internal Server Error responses |


## Custom Dashboard

![](https://raw.github.com/Appdynamics/couchdb-monitoring-extension/master/CouchDB%20Dashboard.png)

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com/t5/eXchange/CouchDB-Monitoring-Extension/idi-p/5537) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).

