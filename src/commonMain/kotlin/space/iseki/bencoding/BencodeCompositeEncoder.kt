package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder

interface BencodeCompositeEncoder : CompositeEncoder {
    val options: BencodeOptions
    fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray)
    fun encodeBinaryStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        strategy: BinaryStringStrategy,
        value: String,
    )

    fun reportError(message: String, descriptor: SerialDescriptor, index: Int): Nothing =
        throw BencodeEncodeException.of(message, descriptor, index)
}

