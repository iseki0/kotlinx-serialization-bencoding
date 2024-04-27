package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder

interface BencodeCompositeDecoder: CompositeDecoder {
    fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray
}