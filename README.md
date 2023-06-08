# kotlinx-serialization-bencode

A Kotlin serialization codec for bencoding format. (Bittorrent)

🚧Note: Currently only decoder was implemented.🚧

Reference: [https://www.bittorrent.org/beps/bep_0003.html](https://www.bittorrent.org/beps/bep_0003.html)

## Usage

Add the dependency to your `build.gradle.kts`
```kotlin
dependencies{
    implementation("space.iseki.bencoding:kotlinx-serialization-bencoding:0.1.0")
}
```

```kotlin
@Serialization
data class Meta(val announce: String) // The torrent file format

fun foo(input: InputStream) {
    println(input.decodeInBencoding<Meta>())
}
```

