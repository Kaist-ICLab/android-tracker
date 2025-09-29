@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "android-tracker"

include(":tracker-library")
//include(":phone")
//include(":galaxywatch-monitor")
//include(":galaxywatch")

//include(":test-auth")
//include(":test-permission")
//include(":test-notification")
//include(":test-auth")
//include(":test-listener")
//include(":listener-test-app")
//include(":sensor-test-app")
//include(":test-controller")
//include(":test-galaxywatch-sensor")
include(":test-sensor")
include(":test-survey")
include(":example-wearable-tracker")
//include(":test-sync")
//include(":test-sync-watch")
