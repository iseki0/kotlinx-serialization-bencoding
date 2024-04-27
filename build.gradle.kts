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
    repositories {
        mavenCentral()
    }
    group = "space.iseki.bencoding"
    if (version == "unspecified") version = "0.1.1-SNAPSHOT"
}

dependencies {
    commonMainApi(libs.kotlinx.serialization.core)
    commonTestImplementation(libs.kotlinx.serialization.core)
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
    js {
        browser {
            webpackTask {
//                output.libraryTarget = "commonjs2"
            }
        }
        binaries.executable()
    }
    sourceSets {
        all {
            languageSettings {
//                enableLanguageFeature("ContextReceivers")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
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

