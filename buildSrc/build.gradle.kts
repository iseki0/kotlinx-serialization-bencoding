plugins {
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.8.21")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.8.10")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.1")
    implementation("org.jetbrains.kotlinx:binary-compatibility-validator:0.13.2")
}
