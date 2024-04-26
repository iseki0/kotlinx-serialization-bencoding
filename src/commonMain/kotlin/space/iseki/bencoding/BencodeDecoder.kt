package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

interface BencodeDecoder : CompositeDecoder, Decoder {
    fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray
    fun decodeByteArray(): ByteArray
}

