package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder

interface BencodeCompositeDecoder : CompositeDecoder {
    fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray
    fun reportError(message: String, descriptor: SerialDescriptor, index: Int): Nothing =
        throw BencodeDecodeException(-1, message)

    val options: BencodeOptions
}