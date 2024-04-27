plugins {
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0-RC1")
    implementation("org.jetbrains.kotlin:kotlin-serialization:2.0.0-RC1")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.6")
    implementation("org.jetbrains.kotlinx:binary-compatibility-validator:0.14.0")
}
