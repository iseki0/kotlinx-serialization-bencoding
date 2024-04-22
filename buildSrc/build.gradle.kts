plugins {
    `kotlin-dsl`
    idea
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.22")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.6")
    implementation("org.jetbrains.kotlinx:binary-compatibility-validator:0.14.0")
}
