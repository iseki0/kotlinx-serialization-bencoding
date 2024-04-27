import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    `maven-publish-convention`
}

allprojects {
    group = "space.iseki.bencoding"
    if (version == "unspecified") version = "0.1.1-SNAPSHOT"
}

dependencies {
    commonMainApi(libs.kotlinx.serialization.core)
}

kotlin {
    jvmToolchain(22)
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class) compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.add("-Xjvm-default=all")
        }
        withJava()
    }
    sourceSets {
        all {
            languageSettings {
//                enableLanguageFeature("ContextReceivers")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.withType<JavaCompile> {
    targetCompatibility = "17"
    sourceCompatibility = "17"
}

