import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    idea
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

repositories {
    mavenCentral()
}

kotlin {

    targets.all {
        compilations.all {
            compilerOptions.configure {
                jvmToolchain(17)
//                languageVersion.set(KotlinVersion.KOTLIN_2_0)
            }
        }
    }
    jvm {
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

//    js(IR) {
//        browser {}
//    }

    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
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

fun JavaToolchainSpec.configure() {
    languageVersion.set(JavaLanguageVersion.of(17))
}
