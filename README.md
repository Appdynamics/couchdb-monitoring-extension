CouchDB Monitoring Extension
============================

This eXtension works only with the Java agent.

## Use Case

Apache CouchDB, commonly referred to as CouchDB, is an open source database that focuses on ease of use and on being "a database that completely embraces the web".It is a NoSQL database that uses JSON to store data, JavaScript as its query language using MapReduce, and HTTP for an API. The CouchDB monitoring extension gathers metrics for the specified hosts that have couchDB installed. 

## Installation
<ol>
	<li>Type 'ant package' in the command line from the couchedb-monitoring-extension directory.
	</li>
	<li>Deploy the file couchedbMonitor.zip found in the 'dist' directory into the &lt;machineagent install dir&gt;/monitors/ directory.
	</li>
	<li>Unzip the deployed file.
	</li>
	<li> (OPTIONAL) Open &lt;machineagent install dir&gt;/monitors/CouchDBMonitor/conf/monitor.xml and configure the couchDB parameters.
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
|conf            | Contains the monitor.xml |
|lib             | Contains third-party project references |
|src             | Contains source code of the CouchDB monitoring extension |
|dist            | Only obtained when using ant. Run 'ant build' to get binaries. Run 'ant package' to get the distributable .zip file |
|build.xml       | Ant build script to package the project (required only if changing Java code) |

## Metrics

{couchdb, database_writes}, number of times a database was changed}
{couchdb, database_reads}, number of times a document was read from a database}
{couchdb, open_databases}, number of open databases}
{couchdb, open_os_files}, number of file descriptors CouchDB has open}
{couchdb, request_time}, length of a request inside CouchDB without MochiWeb}

{httpd, bulk_requests}, number of bulk requests}
{httpd, requests}, number of HTTP requests}
{httpd, temporary_view_reads}, number of temporary view reads}
{httpd, view_reads}, number of view reads}

{httpd_request_methods, 'COPY'}, number of HTTP COPY requests}
{httpd_request_methods, 'DELETE'}, number of HTTP DELETE requests}
{httpd_request_methods, 'GET'}, number of HTTP GET requests}
{httpd_request_methods, 'HEAD'}, number of HTTP HEAD requests}
{httpd_request_methods, 'MOVE'}, number of HTTP MOVE requests}
{httpd_request_methods, 'POST'}, number of HTTP POST requests}
{httpd_request_methods, 'PUT'}, number of HTTP PUT requests}

{httpd_status_codes, '200'}, number of HTTP 200 OK responses}
{httpd_status_codes, '201'}, number of HTTP 201 Created responses}
{httpd_status_codes, '202'}, number of HTTP 202 Accepted responses}
{httpd_status_codes, '301'}, number of HTTP 301 Moved Permanently responses}
{httpd_status_codes, '304'}, number of HTTP 304 Not Modified responses}
{httpd_status_codes, '400'}, number of HTTP 400 Bad Request responses}
{httpd_status_codes, '401'}, number of HTTP 401 Unauthorized responses}
{httpd_status_codes, '403'}, number of HTTP 403 Forbidden responses}
{httpd_status_codes, '404'}, number of HTTP 404 Not Found responses}
{httpd_status_codes, '405'}, number of HTTP 405 Method Not Allowed responses}
{httpd_status_codes, '409'}, number of HTTP 409 Conflict responses}
{httpd_status_codes, '412'}, number of HTTP 412 Precondition Failed responses}
{httpd_status_codes, '500'}, number of HTTP 500 Internal Server Error responses}

## Custom Dashboard

![](https://raw.github.com/Appdynamics/couchedb-monitoring-extension/master/couchedb%20Dashboard.png?token=2880440__eyJzY29wZSI6IlJhd0Jsb2I6QXBwZHluYW1pY3MvZWhjYWNoZS1tb25pdG9yaW5nLWV4dGVuc2lvbi9tYXN0ZXIvRWhjYWNoZSBEYXNoYm9hcmQucG5nIiwiZXhwaXJlcyI6MTM4NDM2NzI4Mn0%3D--a6f98fa60151f8b5c0823c39fb52770d147e55bf)

##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/couchedb-monitoring-extension).

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).

