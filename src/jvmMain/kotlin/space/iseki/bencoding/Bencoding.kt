@file:JvmName("BencodingJVM")

package space.iseki.bencoding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import space.iseki.bencoding.internal.BencodeDecoder0
import space.iseki.bencoding.internal.BencodeEncoder0
import space.iseki.bencoding.internal.InputStreamLexer
import space.iseki.bencoding.internal.OutputStreamWriter
import java.io.InputStream

fun <T> Bencode.decodeFromStream(input: InputStream, serializer: KSerializer<T>): T =
    serializer.deserialize(BencodeDecoder0(InputStreamLexer(input), serializersModule, options))

inline fun <reified T> Bencode.decodeFromStream(input: InputStream) = decodeFromStream(input, serializer<T>())

fun <T> Bencode.encodeToStream(value: T, serializer: KSerializer<T>, output: java.io.OutputStream) {
    BencodeEncoder0(serializersModule, options, OutputStreamWriter(output)).encodeSerializableValue(serializer, value)
}

inline fun <reified T> Bencode.encodeToStream(value: T, output: java.io.OutputStream) =
    encodeToStream(value, serializer<T>(), output)
