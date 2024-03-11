@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.ksp)
    id("maven-publish")
}

android {
    namespace = "com.ducktappedapps.grpclogger"
    compileSdk = libs.versions.compile.sdk.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toIntOrNull()

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerVersion.get()
    }
    kapt {
        correctErrorTypes = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appCompat)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.bundles.grpc)

    implementation(libs.bundles.dagger.impl)
    kapt(libs.bundles.dagger.kapt)


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)

    implementation(libs.bundles.paging)
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components.findByName("release"))
            groupId = "org.ducktappedapps"
            artifactId = "grpclogger"
            version = "1.0"

        }
    }
}

