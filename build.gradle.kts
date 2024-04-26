import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

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
    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.apply {
                    add("-Xlambdas=indy")
                    add("-Xjvm-default=all")
                    add("-Xassertions=jvm")
                }
                jvmToolchain(22)
            }
        }
    }
    jvm {
        withJava()
        compilations.all {
            compilerOptions.configure {
                jvmTarget = JvmTarget.JVM_17
            }
        }
    }
    js(KotlinJsCompilerType.IR).browser {}
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

