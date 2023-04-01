# kotlinx-serialization-bencode

A Kotlin serialization codec used to encode/decode bencoding format.

🚧WIP: Currently only decoder is implemented.🚧

Reference: [https://www.bittorrent.org/beps/bep_0003.html](https://www.bittorrent.org/beps/bep_0003.html)

## Usage
```kotlin
@Serialization
data class Meta(val announce: String)

fun foo(input: InputStream){
    println(input.decodeBencode<Meta>())
}
```
