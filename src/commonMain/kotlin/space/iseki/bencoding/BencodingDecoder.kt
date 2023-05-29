@file:Suppress("OPT_IN_USAGE")

package space.iseki.bencoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

internal interface BencodingDecoder : CompositeDecoderHelper, Decoder

internal class BencodingDecoderImpl(private val input: I) : Decoder {
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    private fun error(msg: String): Nothing = throw BencodingSerializationException(msg)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = when (val l = input.lookahead()) {
        Symbol.List -> object : CompositeDecoderHelper, Decoder by this {
            override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
                TODO("Not yet implemented")
            }

            override fun endStructure(descriptor: SerialDescriptor) {
                TODO("Not yet implemented")
            }

        }

        Symbol.Dict -> object : CompositeDecoderHelper, Decoder by this {
            override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
                TODO("Not yet implemented")
            }

            override fun endStructure(descriptor: SerialDescriptor) {
                TODO("Not yet implemented")
            }

        }

        else -> error("unexpected $l, expect a list or dict")
    }

    override fun decodeBoolean(): Boolean = input.readText().decodeToString().toBooleanStrict()

    override fun decodeByte(): Byte = input.readNumber().toByte()

    override fun decodeChar(): Char = throw UnsupportedOperationException()

    override fun decodeDouble(): Double = input.readNumber().toDouble()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        enumDescriptor.getElementIndex(input.readText().decodeToString())

    override fun decodeFloat(): Float = input.readNumber().toFloat()

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    override fun decodeInt(): Int = input.readNumber().toInt()

    override fun decodeLong(): Long = input.readNumber()

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = true

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = null

    override fun decodeShort(): Short = input.readNumber().toShort()

    override fun decodeString(): String = input.readText().decodeToString()
}
