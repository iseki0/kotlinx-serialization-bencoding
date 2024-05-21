@file:JvmName("BencodingJVM")

package space.iseki.bencoding.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import space.iseki.bencoding.Bencode
import java.io.InputStream

fun <T> Bencode.decodeFromStream(input: InputStream, serializer: KSerializer<T>): T =
    serializer.deserialize(BencodeDecoder0(InputStreamLexer(input), serializersModule, options))

inline fun <reified T> Bencode.decodeFromStream(input: InputStream) = decodeFromStream(input, serializer<T>())


