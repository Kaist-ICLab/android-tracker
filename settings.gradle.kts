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
//include(":test-controller")
include(":test-galaxywatch-sensor")
