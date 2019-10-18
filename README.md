AppDynamics CouchDB Monitoring Extension
============================

## Use Case
Apache CouchDB, commonly referred to as CouchDB, is an open source NOSQL database.
The CouchDB Monitoring Extension can monitor multiple CouchDB clusters and display the statistics in AppDynamics Metric Browser.

## Prerequisites
Before the extension is installed, the prerequisites mentioned here need to be met. Please do not proceed with the extension installation if the specified prerequisites are not met.
This extension works only with the standalone Java machine agent. The extension requires the machine agent to be up and running.

## Installation
1. Download and unzip the CouchDBMonitor-version.zip file into `<MACHINE_AGENT_HOME>/monitors/ directory`.
2. Configure the extension by referring to the below section.
3. Restart the machine agent.

## Configuring the extension using config.yml ##
* Configure the CouchDB monitoring extension by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/CouchDBMonitor/`
The metricPrefix of the extension has to be configured as specified [here](https://community.appdynamics.com/t5/Knowledge-Base/Extensions-Prerequisites-Guide/ta-p/35213). 
Please make sure that the right metricPrefix is chosen based on your machine agent deployment, 
otherwise this could lead to metrics not being visible in the controller.
* Enter one node from each of the clusters you are monitoring in the `servers` section. The extension will collect metrics from all nodes in the cluster.
* Configure the CouchDB instances by specifying the URI(required), username(required), password(required) of the CouchDB account, 
encryptedPassword(only if password encryption required), proxy(optional), useSSL(set to true if SSL is required). If SSL is required, 
please configure the `connection` section.
* Any changes to the config.yml does not require the machine agent to be restarted. 

## Filtering the metrics
The extension supports filtering of the metrics based on patterns of the `name` of each CouchDB node. 
The node name is set in CouchDB when CouchDB is initially installed on each node. 
The node name can be found from the [/_membership endpoint](https://docs.couchdb.org/en/latest/cluster/nodes.html).

## Metrics in metrics.xml
* The metrics.xml is pre-configured with the CouchDB metrics from [/_stats endpoint for each node](https://docs.couchdb.org/en/latest/api/server/common.html#get--_node-node-name-_stats).
It can be configured to collect only those metrics that are required.
* For configuring the metrics, the following properties can be used:
  
         | Metric Property   |   Default value |         Possible values         |                                              Description                                                                                                |
         | :---------------- | :-------------- | :------------------------------ | :------------------------------------------------------------------------------------------------------------- |
         | alias             | metric name     | Any string                      | The substitute name to be used in the metric browser instead of metric name.                                   |
         | aggregationType   | "AVERAGE"       | "AVERAGE", "SUM", "OBSERVATION" | [Aggregation qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)    |
         | timeRollUpType    | "AVERAGE"       | "AVERAGE", "SUM", "CURRENT"     | [Time roll-up qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)   |
         | clusterRollUpType | "INDIVIDUAL"    | "INDIVIDUAL", "COLLECTIVE"      | [Cluster roll-up qualifier](https://docs.appdynamics.com/display/PRO44/Build+a+Monitoring+Extension+Using+Java)|
         | multiplier        | 1               | Any number                      | Value with which the metric needs to be multiplied.                                                            |
         | convert           | null            | Any key value map               | Set of key value pairs that indicates the value to which the metrics need to be transformed. eg: UP:0, DOWN:1  |
         | delta             | false           | true, false                     | If enabled, gives the delta values of metrics instead of actual values.   

## Credentials Encryption
Please visit [this](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-Password-Encryption-with-Extensions/ta-p/29397) 
page to get detailed instructions on password encryption. The steps in this document will guide you through the whole process.

## Extensions Workbench
Workbench is an inbuilt feature provided with each extension in order to assist you to fine tune the extension setup before you actually deploy it on the controller. Please review the following
[document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-use-the-Extensions-WorkBench/ta-p/30130) for how to use the Extensions WorkBench

## Troubleshooting
Please follow the steps listed in the [extensions troubleshooting document](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695) in order to troubleshoot your issue. 
These are a set of common issues that customers might have faced during the installation of the extension. If these don't solve your issue, please follow the last step on the troubleshooting-document to contact the support team.

## Support Tickets
If after going through the Troubleshooting Document you have not been able to get your extension working, please file a ticket and add the following information.
Please provide the following in order for us to assist you better.  
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
