AppDynamics CouchDB Monitoring Extension
============================

## Use Case
Apache CouchDB, commonly referred to as CouchDB, is an open-source NoSQL database.
The CouchDB Monitoring Extension can monitor multiple CouchDB clusters and display the statistics in AppDynamics Metric Browser.

## Prerequisites
Before the extension is installed, the prerequisites mentioned [here](https://community.appdynamics.com/t5/Knowledge-Base/Extensions-Prerequisites-Guide/ta-p/35213) need to be met. Please do not proceed with the extension installation if the specified prerequisites are not met.

## Installation
1. Download and unzip the CouchDBMonitor-{version}.zip file into `<MACHINE_AGENT_HOME>/monitors/` directory. 
2. Please place the extension in the "monitors" directory of your Machine Agent installation directory. Do not place the extension in the "extensions" directory of your Machine Agent installation directory.
3. Configure the extension by referring to the below section. The metricPrefix of the extension has to be configured as specified [here](https://community.appdynamics.com/t5/Knowledge-Base/How-do-I-troubleshoot-missing-custom-metrics-or-extensions/ta-p/28695#Configuring%20an%20Extension). Please make sure that the right metricPrefix is chosen based on your machine agent deployment, otherwise this could lead to metrics not being visible in the controller.
4. Restart the machine agent.
5. The extension needs to be able to connect to CouchDB in order to collect and send metrics. To do this, you will have to either establish a remote connection in between the extension and the product, or have an agent on the same machine running the product in order for the extension to collect and send the metrics.

## Configuration 
In order to use this extension, the following files need to be configured - config.yml and metrics.xml. Here's how to configure those files. 

### Config.yml
* Configure the CouchDB monitoring extension by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/CouchDBMonitor/`
* Enter one node from each of the clusters you are monitoring in the `servers` section of the config.yml. The extension will automatically collect metrics from all nodes in the cluster.
* Configure the CouchDB instances by specifying the URI(required), username(required), password(required) of the CouchDB account, 
encryptedPassword(only if password encryption required), proxy(optional), useSSL(set to true if SSL is required). If SSL is required, please configure the `connection` section.
```
servers:
  - uri: "http://couchdb.one:5984"
    username: "admin" # user should have privileges in CouchDB
    password: "admin"
    encryptedPassword: ""
    useSSL: "false"
    displayName: "cluster1"
```
* If you wish to monitor multiple clusters from one extension, please pick one node from each cluster and add them in the servers section.
For example, If you want to monitor two clusters - `cluster1` and `cluster2`, add details of any one node from <b>each</b> cluster. 
```
servers:
  - uri: "http://couchdb.one:5984"
    username: "admin" # user should have privileges in CouchDB
    password: "admin"
    encryptedPassword: ""
    useSSL: "false"
    displayName: "cluster1"   # any one node from cluster1

  - uri: "http://couchdb.two:5984"
    username: "admin" # user should have privileges in CouchDB
    password: "admin"
    encryptedPassword: ""
    useSSL: "false"
    displayName: "cluster1"   # any one node from cluster2

<<To add more clusters, simply add any one node from each cluster here>>
```
 
* Any changes to the config.yml does <b>not</b> require the machine agent to be restarted. 
* Please copy all the contents of the config.yml file and go to http://www.yamllint.com/ . On reaching the website, paste the contents and press the “Go” button on the bottom left.
If you get a valid output, that means your formatting is correct and you may move on to the next step.

### Metrics.xml
* The metrics.xml is a configurable file with the list of all metrics that the extension will fetch. 
* The extension reports primary CouchDB operation metrics from each of your servers.  The metrics reported by the extension are from the `couchdb` group of the [/_stats endpoint for each node](https://docs.couchdb.org/en/latest/api/server/common.html#get--_node-node-name-_stats)
* The metrics.xml is pre-configured with CouchDB metrics from [/_stats endpoint for each node](https://docs.couchdb.org/en/latest/api/server/common.html#get--_node-node-name-_stats). 
* The metrics.xml can be configured to report only those metrics that are required. Please remove or comment out metrics that you don't require. 
* For configuring the metrics, the following properties can be used:

         | Metric Property   |   Default value |         Possible values         |                                              Description                                                       |
         | :---------------- | :-------------- | :------------------------------ | :------------------------------------------------------------------------------------------------------------- |
         | alias             | metric name     | Any string                      | The substitute name to be used in the metric browser instead of metric name.                                   |
         | aggregationType   | "AVERAGE"       | "AVERAGE", "SUM", "OBSERVATION" | [Aggregation qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)    |
         | timeRollUpType    | "AVERAGE"       | "AVERAGE", "SUM", "CURRENT"     | [Time roll-up qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)   |
         | clusterRollUpType | "INDIVIDUAL"    | "INDIVIDUAL", "COLLECTIVE"      | [Cluster roll-up qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)|
         | multiplier        | 1               | Any number                      | Value with which the metric needs to be multiplied.                                                            |
         | convert           | null            | Any key value map               | Set of key value pairs that indicates the value to which the metrics need to be transformed. eg: UP:0, DOWN:1  |
         | delta             | false           | true, false                     | If enabled, gives the delta values of metrics instead of actual values.   |


## Filtering the metrics
* The extension supports the filtering of CouchDB metrics based on patterns of the `name` of each CouchDB node. The `node` name is set in CouchDB when CouchDB is initially installed on each machine. 
* The `nodes` section of the `CouchDbMonitor/config.yml` will help you filter out metrics from specific nodes in your cluster(s).
* It supports wild card matching. Examples:
  ```
  ### This matches all nodes
   - nodes: [".*"] 
  
  ### This matches all node names that start with dev
   - nodes: ["dev.*"] 
  
  ### This matches nothing, no metrics will be fetched
   - nodes: [] 
  
  ### This matches nothing, no metrics will be fetched
   - nodes: [""] # matches nothing, no metrics will be fetched
  ```
* If you want to find out the node name for your CouchDB servers, it can be found from the [/_membership endpoint](https://docs.couchdb.org/en/latest/cluster/nodes.html).

## Credentials Encryption
Please visit [this](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-Password-Encryption-with-Extensions/ta-p/29397) page to get detailed instructions on password encryption. The steps in this document will guide you through the whole process.

## Extensions Workbench
Workbench is an in-built feature provided with each extension in order to assist you to fine tune the extension setup before you actually deploy it on the controller. Please review this [document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-the-Extensions-WorkBench/ta-p/30130) for how to use the Extensions WorkBench.

## Troubleshooting
##### Connectivity to Couch DB
* In order for the extension to collect metrics successfully, the machine agent should be able to reach the CouchDB clusters. 
* To check that, pick one node from each address and get its IP address. 
* Execute this command from your machine agent host. Please replace the IP address with what you have from the previous step.
```
   curl -v -X GET "http://ip.of.any.node.in.cluster:5984/_membership" --user yourusername:yourpassword
```
If your cluster is set-up over SSL, please use the --cacert option to specify your keys. If the curl command gives a 200 OK response, your cluster is reachable from Machine Agent. If not, please ensure connectivity from your machine agent host every CouchDB cluster.

##### Displaying metrics on the AppDynamics Metric Browser
* Please follow the steps listed in the [extensions troubleshooting document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. These are a set of common issues that customers might have faced during the installation of the extension. If these don't solve your issue, please follow the last step on the troubleshooting-document to contact the support team.

## Support Tickets
If after going through the Troubleshooting Document you have not been able to get your extension working, please file a ticket and add the following information.Please provide the following in order for us to assist you better.  
1. Stop the running machine agent .
2. Delete all existing logs under <MachineAgent>/logs .
3. Please enable debug logging by editing the file <MachineAgent>/conf/logging/log4j.xml. Change the level value of the following <logger> elements to debug. 
   ```
   <logger name="com.singularity">
   <logger name="com.appdynamics">
    ```
4. Start the machine agent and please let it run for 10 mins. Then zip and upload all the logs in the directory <MachineAgent>/logs/*.
5. Attach the zipped <MachineAgent>/conf/* directory here.
6. Attach the zipped <MachineAgent>/monitors/<ExtensionMonitor> directory here .
For any support related questions, you can also contact help@appdynamics.com.

## Contributing
Always feel free to fork and contribute any changes directly via [GitHub](https://github.com/Appdynamics/couchdb-monitoring-extension).

## Version
| Name                        |  Version                    | 
| :---------------------------| :---------------------------|
| Extension Version:          | 2.0.0                  |
| Controller Compatibility:   | 2.2 or Later                |
| Tested On:                  | Apache CouchDB 2.2         |
| Operating System Tested On: | Mac OS, Linux               |
| Last updated On:            | Oct 18, 2019          |
| List of changes to this extension| [Change log](https://github.com/Appdynamics/couchdb-monitoring-extension/blob/master/CHANGELOG.md)
