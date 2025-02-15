@file:JvmName("BencodingJVM")

package space.iseki.bencoding.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import space.iseki.bencoding.Bencode
import space.iseki.bencoding.decodeFromStream
import space.iseki.bencoding.encodeToStream
import java.io.InputStream

@Deprecated(
    "Use function in space.iseki.bencoding package", ReplaceWith(
        expression = "decodeFromStream(input, serializer)", imports = ["space.iseki.bencoding.decodeFromStream"]
    ), DeprecationLevel.HIDDEN
)
fun <T> Bencode.decodeFromStream(input: InputStream, serializer: KSerializer<T>): T =
    decodeFromStream(input, serializer)

@Deprecated(
    message = "Use function in space.iseki.bencoding package",
    level = DeprecationLevel.HIDDEN,
    replaceWith = ReplaceWith(
        expression = "decodeFromStream(input)", imports = ["space.iseki.bencoding.decodeFromStream"]
    )
)
inline fun <reified T> Bencode.decodeFromStream(input: InputStream): T = decodeFromStream(input, serializer<T>())

@Deprecated(
    message = "Use function in space.iseki.bencoding package",
    level = DeprecationLevel.HIDDEN,
    replaceWith = ReplaceWith(
        expression = "encodeToStream(value, serializer, output)", imports = ["space.iseki.bencoding.encodeToStream"]
    )
)
fun <T> Bencode.encodeToStream(value: T, serializer: KSerializer<T>, output: java.io.OutputStream) {
    encodeToStream(value, serializer, output)
}

@Deprecated(
    message = "Use function in space.iseki.bencoding package",
    level = DeprecationLevel.HIDDEN,
    replaceWith = ReplaceWith(
        expression = "encodeToStream(value, output)", imports = ["space.iseki.bencoding.encodeToStream"]
    )
)
inline fun <reified T> Bencode.encodeToStream(value: T, output: java.io.OutputStream) =
    encodeToStream(value, serializer<T>(), output)
