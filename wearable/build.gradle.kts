plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "kaist.iclab.wearablelogger"
    compileSdk = 34

    defaultConfig {
        applicationId = "kaist.iclab.wearablelogger"
        minSdk = 30
        targetSdk = 30
        versionCode = 1
        versionName = "2023-11-08"
        vectorDrawables {3334
            useSupportLibrary = true
        }

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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

//  Default libraries for use of Android and Kotlin.
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.gms:play-services-wearable:18.1.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

//  Include privileged SDK
    implementation(fileTree("libs"))

//  Jetpack Compose is a modern declarative UI Toolkit for Android
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.wear.compose:compose-material:1.3.0")
    implementation("androidx.wear.compose:compose-foundation:1.3.0")
    implementation("androidx.compose.material:material-icons-extended")

//  Horologist is a group of libraries that aim to supplement Wear OS developers with features that are commonly required by developers but not yet available.
//  https://github.com/google/horologist
    implementation("com.google.android.horologist:horologist-compose-tools:0.1.5")

//  Dependency for Koin Library
//  https://insert-koin.io/
    implementation("io.insert-koin:koin-android:3.5.0")
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")


    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")

    val room_version = "2.6.1"
    // RoomDB
    implementation("androidx.room:room-runtime:${room_version}")
    implementation("androidx.room:room-ktx:${room_version}")
    annotationProcessor("androidx.room:room-compiler:${room_version}")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:${room_version}")
//  Dependency for testing.
//  2023-11-08: It is not required for current development progress, but should be added for future testing.
//    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    //    implementation("com.google.android.horologist:horologist-tiles:0.1.5")
//    implementation("androidx.wear.watchface:watchface-complications-data-source-ktx:1.1.1")
    //    implementation("androidx.wear.tiles:tiles:1.2.0")
//    implementation("androidx.wear.tiles:tiles-material:1.2.0")
    //    implementation("androidx.percentlayout:percentlayout:1.0.0")
//    implementation("androidx.legacy:legacy-support-v4:1.0.0")
//    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.10")
}