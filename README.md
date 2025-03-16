# kotlinx-serialization-bencode

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/iseki0/kotlinx-serialization-bencoding/build.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/space.iseki.bencoding/kotlinx-serialization-bencoding)](https://central.sonatype.com/artifact/space.iseki.bencoding/kotlinx-serialization-bencoding)
![License](https://img.shields.io/github/license/iseki0/kotlinx-serialization-bencoding)

A Kotlin serialization codec for bencoding format. (Bittorrent)

Reference: [https://www.bittorrent.org/beps/bep_0003.html](https://www.bittorrent.org/beps/bep_0003.html)

## Usage

Add the dependency to your `build.gradle.kts`

```kotlin
dependencies {
  implementation("space.iseki.bencoding:kotlinx-serialization-bencoding:0.2.7")
}
```

```kotlin
@Serialization
data class Meta(val announce: String) // The torrent file format

fun foo(input: InputStream) {
    println(Bencode.decodeFromStream<Meta>(data.inputStream()))
}
```

## Bencoding Format

The following content is copied
from [https://www.bittorrent.org/beps/bep_0003.html](https://www.bittorrent.org/beps/bep_0003.html) for a memo.

- Strings are length-prefixed base ten followed by a colon and the string. For example `4:spam` corresponds to 'spam'.
- Integers are represented by an 'i' followed by the number in base 10 followed by an 'e'. For example `i3e` corresponds
  to 3 and `i-3e` corresponds to -3. Integers have no size limitation. `i-0e` is invalid. All encodings with a leading
  zero,
  such as `i03e`, are invalid, other than i0e, which of course corresponds to 0.
- Lists are encoded as an 'l' followed by their elements (also bencoded) followed by an 'e'. For
  example `l4:spam4:eggse`
  corresponds to ['spam', 'eggs'].
- Dictionaries are encoded as a 'd' followed by a list of alternating keys and their corresponding values followed by
  an 'e'. For example, `d3:cow3:moo4:spam4:eggse` corresponds to {'cow': 'moo', 'spam': 'eggs'} and `d4:spaml1:a1:bee`
  corresponds to {'spam': ['a', 'b']}. Keys must be strings and appear in sorted order (sorted as raw strings, not
  alphanumerics).