package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder

internal interface CompositeDecoderHelper : CompositeDecoder {

    fun decodeSegment(descriptor: SerialDescriptor, index: Int): ByteArray
    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        decodeStringElement(descriptor, index).toBoolean()

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        decodeStringElement(descriptor, index).toByte()

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        decodeStringElement(descriptor, index)[0]

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        decodeStringElement(descriptor, index).toDouble()

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        decodeStringElement(descriptor, index).toFloat()

    //    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder
    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        decodeStringElement(descriptor, index).toShort()

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        decodeSegment(descriptor, index).decodeToString()

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        decodeStringElement(descriptor, index).toInt()

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        decodeStringElement(descriptor, index).toLong()

}

