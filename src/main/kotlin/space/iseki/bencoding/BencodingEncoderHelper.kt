package space.iseki.bencoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

internal interface BencodingEncoderHelper : BencodingEncoder {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) = encodeBoolean(value)

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) = encodeByte(value)

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) = encodeChar(value)

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) = encodeDouble(value)

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) = encodeFloat(value)

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder = encodeInline(descriptor)

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) = encodeInt(value)

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) = encodeLong(value)

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) = serializer.serialize(this, value)

    @ExperimentalSerializationApi
    override fun encodeNull() {
        encodeString("null")
    }

    override fun encodeBoolean(value: Boolean) {
        encodeString(value.toString())
    }

    override fun encodeByte(value: Byte) {
        encodeInteger(value)
    }

    override fun encodeChar(value: Char) {
        encodeString(value.toString())
    }

    override fun encodeDouble(value: Double) {
        encodeString(value.toString())
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        serializer.serialize(this, value)
    }

    override fun encodeFloat(value: Float) {
        encodeString(value.toString())
    }

    override fun encodeInt(value: Int) {
        encodeInteger(value)
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        TODO()
    }

    override fun encodeLong(value: Long) {
        encodeInteger(value)
    }

    override fun encodeShort(value: Short) {
        encodeInteger(value)
    }

    override fun encodeString(value: String) {
        encodeText(value.encodeToByteArray())
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        return this
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) = encodeShort(value)

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) = encodeString(value)
}
