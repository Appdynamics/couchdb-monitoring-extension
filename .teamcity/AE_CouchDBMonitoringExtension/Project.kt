package AE_CouchDBMonitoringExtension

import AE_CouchDBMonitoringExtension.buildTypes.*
import AE_CouchDBMonitoringExtension.vcsRoots.AE_CouchDBMonitoringExtension
import jetbrains.buildServer.configs.kotlin.v2018_2.Project
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.VersionedSettings.BuildSettingsMode.PREFER_SETTINGS_FROM_VCS
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.VersionedSettings.Format.KOTLIN
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.VersionedSettings.Mode.ENABLED
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.versionedSettings

object Project : Project({
    uuid = "c7184c27-da9f-41cc-b25a-c026cba93bdd"
    id("AE_CouchDBMonitoringExtension")
    parentId("AE")
    name = "AE_CouchDBMonitoringExtension"

    vcsRoot(AE_CouchDBMonitoringExtension)
    buildType(AE_CouchDBMonitoringExtension_Build)
    buildType(AE_CouchDBMonitoringExtension_IntegrationTests)
    buildType(AE_CouchDBMonitoringExtension_WorkbenchTest)

    features {
        versionedSettings {
            mode = ENABLED
            buildSettingsMode = PREFER_SETTINGS_FROM_VCS
            rootExtId = "${AE_CouchDBMonitoringExtension.id}"
            showChanges = true
            settingsFormat = KOTLIN
            storeSecureParamsOutsideOfVcs = true
        }
    }

    buildTypesOrder = arrayListOf(
            AE_CouchDBMonitoringExtension_Build,
            AE_CouchDBMonitoringExtension_IntegrationTests,
            AE_CouchDBMonitoringExtension_WorkbenchTest
    )
})