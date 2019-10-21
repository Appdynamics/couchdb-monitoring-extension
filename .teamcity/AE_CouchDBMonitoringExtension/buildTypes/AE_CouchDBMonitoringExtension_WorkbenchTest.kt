package AE_CouchDBMonitoringExtension.buildTypes

import AE_CouchDBMonitoringExtension.publishCommitStatus
import AE_CouchDBMonitoringExtension.vcsRoots.AE_CouchDBMonitoringExtension
import AE_CouchDBMonitoringExtension.withDefaults
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildStep
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.exec
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object AE_CouchDBMonitoringExtension_WorkbenchTest : BuildType({
    uuid = "f4d83d26-5989-48b4-bd86-fdb27f78a469"
    name = "Test Workbench mode"

    withDefaults()

    steps {
        exec {
            path = "make"
            arguments = "workbenchTest"
        }
        exec {
            executionMode = BuildStep.ExecutionMode.ALWAYS
            path = "make"
            arguments = "dockerClean"
        }
    }

    triggers {
        vcs {
        }
    }

    dependencies {
        dependency(AE_CouchDBMonitoringExtension_Build) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
            artifacts {
                artifactRules = """
                +:target/CouchDBMonitor-*.zip => target/
            """.trimIndent()
            }
        }
    }

    publishCommitStatus()
})