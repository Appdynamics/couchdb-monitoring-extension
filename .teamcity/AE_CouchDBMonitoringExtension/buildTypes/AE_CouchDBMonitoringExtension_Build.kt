package AE_CouchDBMonitoringExtension.buildTypes

import AE_CouchDBMonitoringExtension.publishCommitStatus
import AE_CouchDBMonitoringExtension.vcsRoots.AE_CouchDBMonitoringExtension
import AE_CouchDBMonitoringExtension.withDefaults
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object AE_CouchDBMonitoringExtension_Build : BuildType({
    uuid = "d3f682a8-e32c-4c63-a8f0-22a01b0c97d9"
    name = "CouchDB Monitor Build"

    withDefaults()

    steps {
        maven {
            goals = "clean install"
            mavenVersion = defaultProvidedVersion()
            jdkHome = "%env.JDK_18%"
        }
    }

    triggers {
        vcs {
        }
    }

    artifactRules = """
    +:target/CouchDBMonitor-*.zip => target/
""".trimIndent()

    publishCommitStatus()
})