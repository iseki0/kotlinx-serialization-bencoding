package space.iseki.bencoding

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

internal class BencodingDecoderImpl(
    override val serializersModule: SerializersModule,
    private val lexer: Lexer,
) : BencodingDecoder {
    private val states = ArrayDeque<Int>()

    private fun unexpect(message: String): Nothing = throw BencodingDecodeException(message)
    private fun unexpect(token: TokenInstance, vararg expects: TokenInstance): Nothing =
        unexpect("unexpected token $token, expects: ${expects.joinToString()}")

    private fun listStart() {
        states.addLast(0)
    }

    private fun listNextIndex() = states.removeLast().also { check(it > -1);states.addLast(it + 1) }

    private fun dictStart() {
        states.addLast(-1)
    }

    private fun skipCurrentStructure() {
        var counter = 1
        while (counter > 0) {
            when (lexer.consume()) {
                is Token.End -> counter--
                is Token.ListStart,
                is Token.DictStart -> counter++

                is Token.EOF -> unexpect(Token.EOF, Token.End, Token.ListStart, Token.DictStart, Token.Segment)
                is Token.Segment -> Unit
            }
        }
    }

    private fun skipValue() {
        when (val token = lexer.consume()) {
            is Token.Segment -> Unit
            is Token.ListStart,
            is Token.DictStart -> skipCurrentStructure()

            else -> unexpect(token, Token.ListStart, Token.DictStart, Token.Segment)
        }
    }

    override fun decodeSegment(): ByteArray {
        debug("decodeSegment")
        when(val token = lexer.consume()){
            is Token.Segment -> return token.data
            else -> unexpect(token, Token.Segment)
        }
    }

    override fun decodeSegment(descriptor: SerialDescriptor, index: Int): ByteArray = decodeSegment()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        debug("beginStructure")
        when (val token = lexer.consume()) {
            is Token.ListStart -> listStart()
            is Token.DictStart -> dictStart()
            else -> unexpect(token, Token.ListStart, Token.DictStart)
        }
        return this
    }

    private fun decodeElementIndexList(descriptor: SerialDescriptor): Int {
        return when (val token = lexer.la1()) {
            is Token.End -> CompositeDecoder.DECODE_DONE
            is Token.ListStart,
            is Token.DictStart,
            is Token.Segment -> listNextIndex()

            else -> unexpect(token, Token.ListStart, Token.DictStart, Token.End, Token.Segment)
        }
    }

    private fun decodeElementIndexDict(descriptor: SerialDescriptor): Int {
        while (true) {
            when (val token = lexer.la1()) {
                is Token.End -> return CompositeDecoder.DECODE_DONE
                is Token.Segment -> {
                    val key = decodeString()
                    val index = descriptor.getElementIndex(key)
                    if (index != CompositeDecoder.UNKNOWN_NAME) {
                        return index
                    }
                    skipValue()
                }

                else -> unexpect(token, Token.End, Token.Segment)
            }
        }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        debug("decodeElementIndex")
        if (states.isEmpty()) unexpect("must in a structure")
        return if (states.last() == -1) {
            decodeElementIndexDict(descriptor)
        } else {
            decodeElementIndexList(descriptor)
        }
    }

    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder = this

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? = decodeSerializableElement(descriptor, index, deserializer, previousValue)

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T = deserializer.deserialize(this)

    override fun endStructure(descriptor: SerialDescriptor) {
        debug("endStructure")
        skipCurrentStructure()
        states.removeLast()
    }

}
