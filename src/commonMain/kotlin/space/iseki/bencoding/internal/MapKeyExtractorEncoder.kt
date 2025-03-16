package space.iseki.bencoding.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import space.iseki.bencoding.BencodeEncoder
import space.iseki.bencoding.BencodeOptions
import space.iseki.bencoding.BinaryStringStrategy

internal class MapKeyExtractorEncoder(
    private val parent: BencodeEncoder,
    val descriptor: SerialDescriptor,
    val index: Int,
) : BencodeEncoder {
    override val serializersModule: SerializersModule
        get() = parent.serializersModule
    override val options: BencodeOptions
        get() = parent.options

    override fun encodeByteArray(bytes: ByteArray) {
        keyMustBeString()
    }

    override fun encodeBinaryString(strategy: BinaryStringStrategy, value: String) {
        options.binaryStringStrategy.encodeString(this, strategy, value)
    }

    var key: String? = null

    private fun keyMustBeString(): Nothing =
        parent.reportError("key must be string(in ${descriptor.serialName}/[$index]${descriptor.getElementName(index)})")

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        keyMustBeString()
    }

    override fun encodeBoolean(value: Boolean) {
        keyMustBeString()
    }

    override fun encodeByte(value: Byte) {
        keyMustBeString()
    }

    override fun encodeChar(value: Char) {
        keyMustBeString()
    }

    override fun encodeDouble(value: Double) {
        keyMustBeString()
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encodeString(enumDescriptor.getElementName(index))
    }

    override fun encodeFloat(value: Float) {
        keyMustBeString()
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder = this

    override fun encodeInt(value: Int) {
        keyMustBeString()
    }

    override fun encodeLong(value: Long) {
        keyMustBeString()
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        keyMustBeString()
    }

    override fun encodeShort(value: Short) {
        keyMustBeString()
    }

    override fun encodeString(value: String) {
        key = value
    }
}
