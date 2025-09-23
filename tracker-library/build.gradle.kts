plugins {
    id("dev.iclab.android.basic.library")
    /* Parceler (for Samsung Health Data SDK) */
    id("kotlin-parcelize")
    alias(libs.plugins.kotlinCompose)
    kotlin("plugin.serialization") version "2.2.10"
}

android {
    namespace = "kaist.iclab.tracker"
}

dependencies {
    implementation(kotlin("reflect"))
    /* Android Compose (for survey) */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.lifecycle.viewmodel)
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    /* Google Authentication */
    api(platform(libs.firebase.bom))
    implementation(libs.bundles.auth)

    /* Local Database*/
    implementation(libs.gson)
    implementation(libs.couchbase)

    /* Location */
    implementation(libs.android.gms.location)

    /* Data sync */
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.android.gms.wearable)
    implementation(libs.kotlinx.coroutines.play.services)

//    implementation(libs.android.gms.fitness)
    /* Network */
    implementation(libs.firebase.messaging)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    /* Samsung Health Sensor SDK & Data SDK from Samsung */
    implementation(fileTree("libs"))
}

//val libraryVersion: String by project

//publishing{
//    publications{
//        register<MavenPublication>("release") {
//        groupId = "kaist.iclab"
//        artifactId = "tracker"
//        version = libraryVersion
//
//        afterEvaluate {
//            from(components["release"])
//        }
//    }
//    }
//    repositories {
//        maven {
//            url = uri("https://jitpack.io")
//        }
//    }
//}