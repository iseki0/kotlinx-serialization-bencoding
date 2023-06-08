plugins {
    `kmm-convention`
    `maven-publish-convention`
}

allprojects {
    group = "space.iseki.bencoding"
    if (version == "unspecified") version = "0.1.1-SNAPSHOT"
}

dependencies {
    commonMainApi(libs.kotlinx.serialization.core)
}
