package space.iseki.bencoding

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


internal interface BencodingDecoderHelper : BencodingDecoder {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean = decodeBoolean()

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte = decodeByte()

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char = decodeChar()

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double = decodeDouble()

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float = decodeFloat()

    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = decodeInline(descriptor)

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int = decodeInt()

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long = decodeLong()

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? = decodeNullableSerializableValue(deserializer)

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T = deserializer.deserialize(this)

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short = decodeShort()

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String = decodeString()

    fun fail(reason: String): Nothing

    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    override fun decodeByte(): Byte = decodeString().toByte()

    override fun decodeChar(): Char = decodeString().toInt().toChar()

    override fun decodeDouble(): Double = decodeString().toDouble()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = enumDescriptor.getElementIndex(decodeString())

    override fun decodeFloat(): Float = decodeString().toFloat()

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    override fun decodeInt(): Int = decodeString().toInt()

    override fun decodeLong(): Long = decodeString().toLong()

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = true

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = if (decodeString() == "null") null else fail("decode null value")

    override fun decodeString(): String = decodeSegment().decodeToString()

    override fun decodeShort(): Short = decodeString().toShort()

}
