package space.iseki.bencoding

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

internal class BencodingDecoderImpl(private val input: I) : DecoderHelper {
    override fun decodeBytes(): ByteArray {
        return input.readText()
    }

    override fun decodeNumber(): Long {
        return input.readNumber()
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return when (val s = input.lookahead()) {
            Symbol.Dict -> {
                input.skip()
                object : CompositeDecoderHelper, DecoderHelper by this {
                    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
                        do {
                            when (val s = input.lookahead()) {
                                Symbol.Text -> {
                                    val idx = descriptor.getElementIndex(decodeString())
                                    if (idx == CompositeDecoder.UNKNOWN_NAME) {
                                        skipCurrentValue()
                                    } else {
                                        return idx
                                    }
                                }

                                Symbol.End -> return CompositeDecoder.DECODE_DONE
                                else -> throw BencodingSerializationException("expect a string(dict key), get $s at ${input.pos}")
                            }
                        } while (true)
                    }

                    override fun endStructure(descriptor: SerialDescriptor) {
                        skipCurrentStructure()
                    }

                }
            }

            Symbol.List -> {
                input.skip()
                object : CompositeDecoderHelper, DecoderHelper by this {
                    var i = 0

                    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
                        return when (input.lookahead()) {
                            Symbol.End -> CompositeDecoder.DECODE_DONE
                            Symbol.EOF -> throw BencodingSerializationException("EOF when decode list")
                            else -> i++

                        }
                    }

                    override fun endStructure(descriptor: SerialDescriptor) {
                        skipCurrentStructure()
                    }
                }
            }

            else -> throw BencodingSerializationException("expect a structured token, get $s at ${input.pos}")
        }
    }

    private fun skipCurrentValue() {
        var counter = 0
        do {
            when (input.lookahead()) {
                Symbol.End -> counter--
                Symbol.Dict, Symbol.List -> counter++
                Symbol.EOF -> break
                else -> Unit
            }
            input.skip()
        } while (counter > 0)
    }

    private fun skipCurrentStructure() {
        var counter = 1
        while (counter > 0) {
            when (input.lookahead()) {
                Symbol.End -> counter--
                Symbol.Dict, Symbol.List -> counter++
                Symbol.EOF -> break
                else -> Unit
            }
            input.skip()
        }
    }
}

interface BencodingDecoder {
    fun decodeBytes(): ByteArray
    fun decodeNumber(): Long
}

private interface DecoderHelper : Decoder, BencodingDecoder {
    override fun decodeBytes(): ByteArray
    override fun decodeNumber(): Long

    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun decodeString(): String = decodeBytes().decodeToString()
    override fun decodeBoolean(): Boolean = decodeString().toBooleanStrict()
    override fun decodeByte(): Byte = decodeNumber().toByte()
    override fun decodeChar(): Char = decodeNumber().toInt().toChar()
    override fun decodeDouble(): Double = decodeNumber().toDouble()
    override fun decodeFloat(): Float = decodeNumber().toFloat()
    override fun decodeInt(): Int = decodeNumber().toInt()
    override fun decodeLong(): Long = decodeNumber()
    override fun decodeShort(): Short = decodeNumber().toShort()
    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = throw UnsupportedOperationException("decode null is not supported")

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = false

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? =
        deserializer.deserialize(this)

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        deserializer.deserialize(this)

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        throw UnsupportedOperationException("decode enum is not supported")
}

private interface CompositeDecoderHelper : CompositeDecoder, Decoder {
    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean = decodeBoolean()
    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte = decodeByte()
    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char = decodeChar()
    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double = decodeDouble()
    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float = decodeFloat()
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = decodeInline(descriptor)
    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int = decodeInt()
    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long = decodeLong()
    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short = decodeShort()
    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String = decodeString()

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? = deserializer.deserialize(this)

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T = deserializer.deserialize(this)
}


