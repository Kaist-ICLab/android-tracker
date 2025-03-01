plugins {
    id("dev.iclab.android.application")
}

android {
    namespace = "kaist.iclab.field_tracker"
}

dependencies {
    implementation(project(":tracker-library"))
}

//plugins {
//    alias(libs.plugins.androidApplication)
//    alias(libs.plugins.jetbrainsKotlinAndroid)
//    alias(libs.plugins.kotlinCompose)
//
//}
//
//android {
//    namespace = "kaist.iclab.field_tracker"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "kaist.iclab.field_tracker"
//        minSdk = 26
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        vectorDrawables {
//            useSupportLibrary = true
//        }
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//    kotlinOptions {
//        jvmTarget = JavaVersion.VERSION_17.majorVersion
//    }
//    buildFeatures {
//        compose = true
//    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.1"
//    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
//}
//
//dependencies {
//    implementation(kotlin("reflect"))
//
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.lifecycle.runtime.ktx)
//    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation(libs.androidx.compose.lifecycle.viewmodel)
//    implementation(libs.androidx.activity.compose)
////    implementation(platform(libs.androidx.compose.bom))
////    implementation(libs.androidx.ui)
////    implementation(libs.androidx.ui.graphics)
////    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material3)
//    implementation(libs.androidx.navigation.runtime.ktx)
//    implementation(libs.androidx.navigation.compose)
//
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//
//
//    implementation(libs.android.gms.location)
//
//    implementation(platform(libs.koin.bom))
//    implementation(libs.koin.android)
//    implementation(libs.koin.core)
//    implementation(libs.koin.androidx.compose)
//
//    debugImplementation(libs.androidx.ui.tooling)
//    implementation(libs.androidx.ui.tooling.preview)
//    implementation(libs.androidx.material.icons.extended)
//
//    implementation(libs.couchbase)
//
//    implementation(project(":tracker-library"))
//}