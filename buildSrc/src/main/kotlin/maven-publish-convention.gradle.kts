plugins {
    id("org.jetbrains.dokka")
    signing
    `maven-publish`
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

publishing {
    repositories {
        maven {
            name = "Central"
            url = if (version.toString().endsWith("SNAPSHOT")) {
                // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                uri("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            }
            credentials {
                username = properties["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = properties["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications {
        withType<MavenPublication> {
            val publication = this
            val javadocJar = tasks.register("${publication.name}JavadocJar", Jar::class) {
                archiveClassifier.set("javadoc")
                from(tasks.dokkaJavadoc)
                // Each archive name should be distinct. Mirror the format for the sources Jar tasks.
                archiveBaseName.set("${archiveBaseName.get()}-${publication.name}")
            }

            artifact(javadocJar)
            pom {
                name.set("kotlinx-serialization-bencoding")
                description.set("A Kotlin serialization codec for bencoding format.")
                url.set("https://github.com/iseki0/kotlinx-serialization-bencoding")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("iseki0")
                        name.set("iseki zero")
                        email.set("iseki@iseki.space")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/iseki0/kotlinx-serialization-bencoding.git")
                    developerConnection.set("scm:git:https://github.com/iseki0/kotlinx-serialization-bencoding.git")
                    url.set("https://github.com/iseki0/kotlinx-serialization-bencoding")
                }
            }
        }
    }

}

signing {
    // To use local gpg command, configure gpg options in ~/.gradle/gradle.properties
    // reference: https://docs.gradle.org/current/userguide/signing_plugin.html#example_configure_the_gnupgsignatory
    useGpgCmd()
    publishing.publications.forEach { sign(it) }
}
