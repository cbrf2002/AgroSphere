plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization.json)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    signingConfigs {
        create("release") {
            storeFile =
                file(System.getenv("KEYSTORE_PATH"))
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    namespace = "com.fsvdevs.agrosphere"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fsvdevs.agrosphere"
        minSdk = 29
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 240
        versionName = "1.36"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
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
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.mpandroidchart)
    implementation(libs.navigation.compose)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.base)
    implementation(libs.protobuf.javalite)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.firebase.bom))
    ksp(libs.androidx.room.compiler)

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
    implementation("com.google.firebase:firebase-analytics-ktx") {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Exclude protobuf-java
    }
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

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles.set(listOf(rootProject.layout.projectDirectory.file("stability_config.conf")))
}

configurations.all {
    resolutionStrategy {
        force("com.google.protobuf:protobuf-javalite:3.25.1") // Force the specific version
    }
}