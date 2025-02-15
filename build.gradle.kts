import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI
import java.net.URL
import java.util.*

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.kotlinx.binary.compatibility.validator)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
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
        browser()
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

publishing {
    repositories {
        maven {
            name = "Central"
            afterEvaluate {
                url = if (version.toString().endsWith("SNAPSHOT")) {
                    // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                    uri("https://oss.sonatype.org/content/repositories/snapshots")
                } else {
                    // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                }
            }

            credentials {
                username = properties["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = properties["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publications {
        withType<MavenPublication> {
            val pubName = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else "$it" }
            val emptyJavadocJar by tasks.register<Jar>("emptyJavadocJar$pubName") {
                archiveClassifier = "javadoc"
                archiveBaseName = artifactId
            }
            artifact(emptyJavadocJar)
            pom {
                val projectUrl = "https://github.com/iseki0/kotlinx-serialization-bencoding"
                name = "kotlinx-serialization-bencoding"
                description = "A Kotlin serialization codec for bencoding format."
                url = projectUrl
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }
                developers {
                    developer {
                        id = "iseki0"
                        name = "iseki zero"
                        email = "iseki@iseki.space"
                    }
                }
                inceptionYear = "2024"
                scm {
                    connection = "scm:git:$projectUrl.git"
                    developerConnection = "scm:git:$projectUrl.git"
                    url = projectUrl
                }
                issueManagement {
                    system = "GitHub"
                    url = "$projectUrl/issues"
                }
                ciManagement {
                    system = "GitHub"
                    url = "$projectUrl/actions"
                }
            }
        }
    }
}

afterEvaluate {
    signing {
        // To use local gpg command, configure gpg options in ~/.gradle/gradle.properties
        // reference: https://docs.gradle.org/current/userguide/signing_plugin.html#example_configure_the_gnupgsignatory
        useGpgCmd()
        publishing.publications.forEach { sign(it) }
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.withType<DokkaTask> {
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = project.layout.projectDirectory.dir("src").asFile
            val p =
                project.layout.projectDirectory.dir("src").asFile.relativeTo(rootProject.layout.projectDirectory.asFile)
                    .toString()
                    .replace('\\', '/')
            remoteUrl = URI.create("https://github.com/iseki0/kotlinx-serialization-bencoding/tree/master/$p").toURL()
            remoteLineSuffix = "#L"
        }
        externalDocumentationLink {
            url = URI.create("https://kotlinlang.org/api/kotlinx.serialization/").toURL()
        }
    }
}

