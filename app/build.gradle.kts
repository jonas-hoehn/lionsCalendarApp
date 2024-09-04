
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.jcoding.lionsweihnachtskalender"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jcoding.lionsweihnachtskalender"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {

    implementation ("androidx.core:core-ktx:1.12.0")

    //Firebase Implementation
    implementation(libs.firebase.bom)
    implementation(libs.firebase.database)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.ui.test.android)
    implementation("androidx.compose.material:material-icons-extended:1.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation(libs.firebase.auth.ktx)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")



    //Navigation Compose
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended.android)

    // Camera
    implementation ("androidx.camera:camera-camera2:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-lifecycle:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-view:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-extensions:1.3.4")

    val cameraxVersion = "1.3.0-rc01"

    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")

    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")
    //implementation (libs.text.recognition)

    //Accompanist
    implementation(libs.accompanist.permissions)
    
    //ML Kit
    implementation("com.google.mlkit:text-recognition:16.0.0")

    //Splash Screen Animation
    implementation("androidx.core:core-splashscreen:1.0.1")

    //Pull down to fresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.28.0")
    implementation(platform(libs.androidx.compose.bom.v2023080))
    implementation ("androidx.compose.material:material:1.3.0")

}


