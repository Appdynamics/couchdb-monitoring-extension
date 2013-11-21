CouchDB Monitoring Extension
============================
This eXtension works only with the standalone machine agent.


## Use Case

Apache CouchDB, commonly referred to as CouchDB, is an open source database that focuses on ease of use and on being "a database that completely embraces the web." It is a NoSQL database that uses JSON to store data, uses JavaScript as its query language using MapReduce, and uses HTTP for an API. The CouchDB monitoring extension gathers metrics for the specified hosts that have couchDB installed. 

## Installation
<ol>
	<li>Type 'ant package' in the command line from the couchdb-monitoring-extension directory.
	</li>
	<li>Deploy the file couchdbMonitor.zip found in the 'dist' directory into the &lt;machineagent install dir&gt;/monitors/ directory.
	</li>
	<li>Unzip the deployed file.
	</li>
	<li> (OPTIONAL) Open &lt;machineagent install dir&gt;/monitors/CouchDBMonitor/monitor.xml and configure the couchDB parameters.
<p></p>
<pre>
	&lt;argument name="hosts-config-path" is-required="true" default-value="monitors/CouchDBMonitor/conf/HostsConfig.xml" /&gt;          
</pre>
	</li>	
	<li>Open &lt;machineagent install dir&gt;/monitors/CouchDBMonitor/conf/HostsConfig.xml and configure the CouchDB hosts.
<p>The host id, port, username, and password need to be configured for each host. Here is a sample configuration for one host: </p>
<pre>
	&lt;Host id="localhost" port="5984" username="username" password="password"/&gt;          
</pre>
	</li>	
	<li> Restart the machine agent.
	</li>
	<li>In the AppDynamics Metric Browser, look for: Application Infrastructure Performance | &lt;Tier&gt; | Custom Metrics | CouchDB
	</li>
</ol>

## Directory Structure

| Directory/File | Description |
|----------------|-------------|
|conf            | Contains the monitor.xml file|
|lib             | Contains third-party project references |
|src             | Contains source code of the CouchDB monitoring extension |
|dist            | Only obtained when using ant. Run 'ant build' to get binaries. Run 'ant package' to get the distributable .zip file |
|build.xml       | Ant build script to package the project (required only if changing Java code) |

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

![](https://raw.github.com/Appdynamics/couchdb-monitoring-extension/master/CouchDB%20Dashboard.png?token=2880440__eyJzY29wZSI6IlJhd0Jsb2I6QXBwZHluYW1pY3MvY291Y2hlZGItbW9uaXRvcmluZy1leHRlbnNpb24vbWFzdGVyL0NvdWNoREIgRGFzaGJvYXJkLnBuZyIsImV4cGlyZXMiOjEzODU1ODEyMzV9--06b1379de823f6a92be69ada5cb9bf23635b33e5)

##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/couchedb-monitoring-extension).

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).

