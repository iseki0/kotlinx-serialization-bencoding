package space.iseki.bencoding

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder

internal class BencodingDecoderImpl(private val lexer: BencodingLexer) : BencodingDecoderHelper {

    /**
     * For:
     * - dict: -1
     * - array: next element index
     */
    private val states = ArrayDeque<Int>()

    override fun decodeSegment(): ByteArray =
        (lexer.consume() as? Token.Segment)?.data ?: fail("expect a string/integer")

    private fun decodeMapIndex(descriptor: SerialDescriptor): Int {
        debug("decodeMapIndex")
        do {
            val token = lexer.peek()
            if (token !is Token.Segment) unexpected(token, listOf(Token.Segment))
            val key = token.data.decodeToString()
            val index = descriptor.getElementIndex(key)
            if (index != CompositeDecoder.UNKNOWN_NAME) {
                return index
            }
            skipValue()
        } while (true)
    }

    private fun decodeListIndex(descriptor: SerialDescriptor): Int {
        debug("decodeListIndex")
        return states.removeLast().also { states.addLast(it + 1) }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        debug("decodeElementIndex")
        ensureInStructure()
        return if (states.last() == -1) {
            decodeMapIndex(descriptor) // map
        } else {
            decodeListIndex(descriptor) // list
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        debug("endStructure")
        skipCurrentStructure()
        states.removeLast()
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        debug("beginStructure")
        when (val token = lexer.consume()) {
            is Token.DictStart -> states.addLast(-1)
            is Token.ListStart -> states.addLast(0)
            else -> unexpected(token, listOf(Token.DictStart, Token.ListStart))
        }
        return this
    }


    override fun fail(reason: String): Nothing = throw BencodingDecodeException(reason)
    private fun unexpectedEOF(): Nothing = fail("unexpected EOF")
    private fun unexpected(token: Token, expects: List<Token>): Nothing =
        fail("unexpected token $token, expects: ${expects.joinToString()}")

    private fun ensureInStructure() {
        if (states.isEmpty()) fail("not a structure")
    }

    private fun skipCurrentStructure() {
        debug("skipCurrentStructure")
        var counter = 1
        while (counter > 0) {
            when (lexer.peek()) {
                is Token.EOF -> unexpectedEOF()
                is Token.End -> counter--
                is Token.DictStart, is Token.ListStart -> counter++
                else -> Unit
            }
            lexer.consume()
        }
    }

    private fun skipValue() {
        debug("skipValue")
        when (val token = lexer.consume()) {
            is Token.End -> fail("unexpected token: $token")
            is Token.EOF -> unexpectedEOF()
            is Token.Segment -> Unit
            is Token.DictStart, Token.ListStart -> skipCurrentStructure()
        }
    }
}

private fun unreachable(): Nothing = error("unreachable")
