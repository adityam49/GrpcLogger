plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "com.ducktappedapps.no_op_grpc_logging"
    compileSdk = libs.versions.compile.sdk.get().toIntOrNull()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toIntOrNull()
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
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.bundles.grpc)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("no-op-logging") {
                from(components.findByName("release"))
                groupId = "com.github.adityam49"
                artifactId = "no-op-grpc-logger"
                version = "0.1"
            }
        }
    }
}
