package space.iseki.bencoding.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import space.iseki.bencoding.BencodeEncoder
import space.iseki.bencoding.BencodeOptions
import space.iseki.bencoding.BinaryStringStrategy

internal interface EncoderDelegate : BencodeEncoder {
    val rootRef: BencodeEncoder
    override val serializersModule: SerializersModule
        get() = rootRef.serializersModule
    override val options: BencodeOptions
        get() = rootRef.options

    override fun encodeChar(value: Char) = encodeLong(value.code.toLong())
    override fun encodeByte(value: Byte) = encodeLong(value.toLong())
    override fun encodeShort(value: Short) = encodeLong(value.toLong())
    override fun encodeInt(value: Int) = encodeLong(value.toLong())
    override fun encodeFloat(value: Float) = options.floatStrategy.encodeFloat(this, value)
    override fun encodeDouble(value: Double) = options.doubleStrategy.encodeDouble(this, value)
    override fun encodeBoolean(value: Boolean) = reportError("boolean is not supported")

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    override fun encodeBinaryString(strategy: BinaryStringStrategy, value: String) {
        options.binaryStringStrategy.encodeString(this, strategy, value)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        reportError("null is not supported")
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder = this

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        encodeValue(EncodeValue.Serialized(value, serializer))
    }

    override fun encodeByteArray(bytes: ByteArray) {
        encodeValue(EncodeValue.Bytes(bytes))
    }

    override fun encodeString(value: String) {
        encodeValue(EncodeValue.Text(value))
    }

    override fun encodeLong(value: Long) {
        encodeValue(EncodeValue.Number(value))
    }

    fun encodeValue(value: EncodeValue)
}