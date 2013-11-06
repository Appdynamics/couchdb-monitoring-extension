CouchDB Monitoring Extension
============================

This eXtension works only with the Java agent.

## Use Case


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
<p>The hostId, port, username, and password need to be configured</p>
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

|Metric Name           | Description     |
|----------------------|-----------------|
|averageGetTime    	   | The average time to retrieve the requested item from the cache |
|cacheHits             | The number of times a requested item was found in the cache |
|diskStoreSize         | The size of the disk store |
|evictionCount         | The number of cache evictions, since the cache was created, or statistics were cleared |
|inMemoryHits          | Number of times a requested item was found in the memory store |
|memoryStoreSize       | The size of the memory store |
|misses                | Number of times a requested item was not found in the cache |
|onDiskHits            | Number of kepspace misses per minute |
|size                  | Size of the cache |

## Custom Dashboard

![](https://raw.github.com/Appdynamics/couchedb-monitoring-extension/master/couchedb%20Dashboard.png?token=2880440__eyJzY29wZSI6IlJhd0Jsb2I6QXBwZHluYW1pY3MvZWhjYWNoZS1tb25pdG9yaW5nLWV4dGVuc2lvbi9tYXN0ZXIvRWhjYWNoZSBEYXNoYm9hcmQucG5nIiwiZXhwaXJlcyI6MTM4NDM2NzI4Mn0%3D--a6f98fa60151f8b5c0823c39fb52770d147e55bf)

##Contributing

Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/couchedb-monitoring-extension).

##Community

Find out more in the [AppSphere](http://appsphere.appdynamics.com) community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:ace-request@appdynamics.com).

