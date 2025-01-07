import org.gradle.jvm.tasks.Jar
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
    if (version == "unspecified") version = "0.2.0-SNAPSHOT"
}

dependencies {
    commonMainImplementation(kotlin("stdlib"))
    commonMainApi(libs.kotlinx.serialization.core)
    commonTestImplementation(kotlin("test"))
    commonTestImplementation(libs.kotlinx.serialization.core)
    commonTestImplementation(libs.kotlinx.serialization.json)
}

kotlin {
    // The latest LTS
    jvmToolchain(21)
    jvm {
        compilations.all {
            compilerOptions.configure {
                jvmTarget = JvmTarget.JVM_1_8
                freeCompilerArgs.add("-Xjvm-default=all-compatibility")
            }
        }
        withJava()
    }
    js {
        browser {
            webpackTask {
                output.libraryTarget = "commonjs2"
            }
        }
        binaries.executable()
    }
    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceSets {
        val java9 by java.sourceSets.creating
        val a by configurations.commonMainApi
        configurations.getByName("jvmJava9Api").extendsFrom(a)
    }
}

tasks.getByName("jvmJar", Jar::class) {
    manifest {
        attributes("Multi-Release" to "true")
    }
    into("META-INF/versions/9") {
        val java9 by sourceSets.getting
        from(java9.output)
    }
    into("/") {
        from("/LICENSE")
        from("/NOTICE")
    }
}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<JavaCompile> {
    // Keep consistent with Kotlin options
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}

tasks.getByName("compileJava9Java", JavaCompile::class) {
    targetCompatibility = "1.9"
    sourceCompatibility = "1.9"
}
