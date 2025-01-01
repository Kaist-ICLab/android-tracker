plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "kaist.iclab.tracker"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
//    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.android.gms.location)
    implementation(libs.android.gms.fitness)
    implementation(libs.android.gms.wearable)

    /* Local Databas e*/
    implementation(libs.gson)
    implementation(libs.couchbase)

    /* Google Authentication */
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.services.auth)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)


    /* Network */
    implementation(libs.okhttp)
    implementation(libs.okio)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)


    /* Samsung Health Sensor SDK from Samsung */
    implementation(fileTree("libs"))
}

val libraryVersion: String by project

publishing{
    publications{
        register<MavenPublication>("release") {
        groupId = "kaist.iclab"
        artifactId = "tracker"
        version = libraryVersion

        afterEvaluate {
            from(components["release"])
        }
    }
    }
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
    }
}