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
    commonMainImplementation(kotlin("stdlib"))
    commonMainApi(libs.kotlinx.serialization.core)
    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotlinx.serialization.core)
}

kotlin {
    // The latest LTS
    jvmToolchain(21)
    jvm {
        compilations.all {
            compilerOptions.configure {
                jvmTarget = JvmTarget.JVM_1_8
                freeCompilerArgs.add("-Xjvm-default=all")
            }
        }
        withJava()
    }
    // Temporary disable JS target until Kotlin 2.0 release!
//    js {
//        browser {
//            webpackTask {
//                output.libraryTarget = "commonjs2"
//            }
//        }
//        binaries.executable()
//    }
    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib-js"))
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
    }
}

tasks.withType<JavaCompile> {
    // Keep consistent with Kotlin options
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}

