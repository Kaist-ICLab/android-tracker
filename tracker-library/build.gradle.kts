plugins {
//    alias(libs.plugins.androidLibrary)
    id("dev.iclab.android.basic.library")
//    id("maven-publish")
}

android {
    namespace = "kaist.iclab.tracker"
}

dependencies {
    implementation(kotlin("reflect"))

    api(platform(libs.firebase.bom))
    /* Google Authentication */
    implementation(libs.bundles.auth)

    /* Local Database*/
    implementation(libs.gson)
    implementation(libs.couchbase)


//    implementation(libs.android.gms.location)
//    implementation(libs.android.gms.fitness)
//    implementation(libs.android.gms.wearable)
    /* Network */
//    implementation(libs.okhttp)
//    implementation(libs.okio)
//    implementation(libs.retrofit)
//    implementation(libs.retrofit.gson)

    /* Samsung Health Sensor SDK & Data SDK from Samsung */
//    implementation(fileTree("libs"))
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