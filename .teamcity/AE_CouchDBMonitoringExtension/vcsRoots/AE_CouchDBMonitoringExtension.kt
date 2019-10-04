package AE_CouchDBMonitoringExtension.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object AE_CouchDBMonitoringExtension : GitVcsRoot({
    uuid = "c9e2337f-b42f-4e9e-9808-1cf93c8728b9"
    id("AE_CouchDBMonitoringExtension")
    name = "AE_CouchDBMonitoringExtension"
    url = "ssh://git@bitbucket.corp.appdynamics.com:7999/ae/couchdb-monitoring-extension.git"
    pushUrl = "ssh://git@bitbucket.corp.appdynamics.com:7999/ae/couchdb-monitoring-extension.git"
    authMethod = uploadedKey {
        uploadedKey = "TeamCity BitBucket Key"
    }
    agentCleanPolicy = AgentCleanPolicy.ALWAYS
    branchSpec = """
    +:refs/heads/(master)
    +:refs/(pull-requests/*)/from
    """.trimIndent()
})