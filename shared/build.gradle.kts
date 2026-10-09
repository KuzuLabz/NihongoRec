import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.buildkonfig)
}

buildkonfig {
    packageName = "com.kuzulabz.nihongorec"

    defaultConfigs {
        buildConfigField(STRING, "version", "0.1.0")
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}

kotlin {

    jvm()

    android {
        namespace = "com.kuzulabz.nihongorec.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)

            implementation(libs.koin.android)

            // Onnxruntime
            implementation(libs.onnxruntime.android)
        }
        jvmMain.dependencies {
            implementation(libs.onnxruntime.jvm)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.compose.material3.windowSize)

            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
            implementation(libs.jetbrains.material3.adaptiveNavigation3)

            // MaterialKolor
            implementation(libs.materialKolor)
            implementation(libs.inklet)

            // Koin
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)

            // Datastore
            implementation(libs.datastore.preferences)

            // Navigation 3
            implementation(libs.androidx.navigation3.ui)

            // Onnxruntime
            implementation(libs.onnxruntime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
//        configure(listOf(targets.getByName("android"), targets.getByName("jvm"))) {
//            compilations.all {
//                defaultSourceSet.dependencies {
//                    implementation(libs.onnxruntime)
//                }
//            }
//        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}