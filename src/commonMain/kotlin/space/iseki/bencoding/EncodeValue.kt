package space.iseki.bencoding

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.Encoder

internal sealed interface EncodeValue {
    class Number(val v: Long) : EncodeValue
    class Text(val v: String) : EncodeValue
    class Bytes(val v: ByteArray) : EncodeValue
    class Serialized<T>(private val v: T, val serializer: SerializationStrategy<T>) : EncodeValue {
        fun doEncode(encoder: Encoder) = serializer.serialize(encoder, v)
    }
}
