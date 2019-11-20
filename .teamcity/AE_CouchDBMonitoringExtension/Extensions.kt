package AE_CouchDBMonitoringExtension

import AE_CouchDBMonitoringExtension.vcsRoots.AE_CouchDBMonitoringExtension
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher

fun BuildType.publishCommitStatus() {
    features {
        commitStatusPublisher {
            vcsRootExtId = "${AE_CouchDBMonitoringExtension.id}"
            publisher = bitbucketServer {
                url = "%env.BITBUCKET_SERVER%"
                userName = "%env.BITBUCKET_USERNAME%"
                password = "%env.BITBUCKET_PASSWORD%"
            }
        }
    }
}

fun BuildType.withDefaults() {
    vcs {
        root(AE_CouchDBMonitoringExtension)
        cleanCheckout = true
    }

    requirements {
        matches("env.AGENT_OS", "Linux")
    }
}