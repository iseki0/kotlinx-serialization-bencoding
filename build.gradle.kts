plugins {
    `kmm-convention`
}

allprojects {
    group = "space.iseki.bencoding"
    if (version == "unspecified") version = "0.1.0-SNAPSHOT"
}

dependencies {
    commonMainApi(libs.kotlinx.serialization.core)
}
