@file:JvmName("Bencoding")

package space.iseki.bencoding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import java.io.InputStream

inline fun <reified T> InputStream.decodeInBencoding() = decodeInBencoding(serializer<T>())

fun <T> InputStream.decodeInBencoding(serializer: KSerializer<T>) =
    BencodingDecoderImpl(InputStreamI(this)).decodeSerializableValue(serializer)
