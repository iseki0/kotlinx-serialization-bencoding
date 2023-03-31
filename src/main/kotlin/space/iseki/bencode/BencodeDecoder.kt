package space.iseki.bencode

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer
import java.io.InputStream

interface BencodeDecoder : Decoder, CompositeDecoder {
    fun decodeSegment(): ByteArray
    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    override fun decodeByte(): Byte = decodeString().toByte()

    override fun decodeChar(): Char = decodeString()[0]

    override fun decodeDouble(): Double = decodeString().toDouble()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        enumDescriptor.getElementIndex(decodeString())

    override fun decodeFloat(): Float = decodeString().toFloat()

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    override fun decodeInt(): Int = decodeString().toInt()

    override fun decodeLong(): Long = decodeString().toLong()

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = true

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? {
        TODO("Not yet implemented")
    }

    override fun decodeShort(): Short = decodeString().toShort()

    override fun decodeString(): String = decodeSegment().decodeToString()

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

class BencodeDecodeException(override val message: String) : RuntimeException()

inline fun <reified T> InputStream.decodeBencode() = decodeBencode(serializer<T>())

fun <T> InputStream.decodeBencode(deserializationStrategy: DeserializationStrategy<T>) =
    BencodeDecoderImpl(EmptySerializersModule(), Lexer(WrappedInputStream(this)))
        .let { deserializationStrategy.deserialize(it) }
