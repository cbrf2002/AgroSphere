plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization.json)
    id("com.google.gms.google-services")
}

android {
    signingConfigs {
        create("release") {
            storeFile =
                file("C:\\Users\\cbrf.2002\\Documents\\Capstone\\AgroSphere_app\\agrosphere_key.jks")
            storePassword = "AgroSphereDev1030"
            keyAlias = "agrosphere_key"
            keyPassword = "AgroSphereDev1030"
        }
    }
    namespace = "com.fsvdevs.agrosphere"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fsvdevs.agrosphere"
        minSdk = 29
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/protobuf.meta"
            excludes += "google/protobuf/field_mask.proto"
        }
    }

    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.activity)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.vico.compose.m3)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


    implementation("androidx.credentials:credentials:1.5.0-alpha05")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-alpha05")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-firestore") {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Exclude protobuf-java
    }
    implementation("com.google.firebase:firebase-auth") {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Exclude protobuf-java
    }
    implementation("com.google.firebase:firebase-database") {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Exclude protobuf-java
    }
    implementation("com.google.firebase:firebase-config") {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Exclude protobuf-java
    }

    implementation("com.google.protobuf:protobuf-javalite:3.25.1")

    implementation(libs.androidx.tools.core) {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

configurations.all {
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.25.1") // Force the specific version
    }
}