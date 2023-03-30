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

    override fun decodeSegment(): ByteArray = (lexer.next() as? Token.Segment)?.data ?: fail("expect a string/integer")


    private var alreadyReadEnd = false

    private fun decodeMapIndex(descriptor: SerialDescriptor): Int {
        do {
            when (val v = lexer.next()) {
                is Token.Segment -> {
                    val idx = descriptor.getElementIndex(v.data.decodeToString())
                    if (idx == CompositeDecoder.UNKNOWN_NAME) {
                        skipValue()
                        continue
                    }
                    return idx
                }

                is Token.End -> {
                    alreadyReadEnd = true
                    return CompositeDecoder.DECODE_DONE
                }

                else -> fail("unexpected token, $v")
            }
        } while (false)
        unreachable()
    }

    private fun decodeListIndex(descriptor: SerialDescriptor): Int {
        return when (lexer.next()) {
            is Token.End -> {
                alreadyReadEnd = true
                CompositeDecoder.DECODE_DONE
            }

            is Token.EOF -> fail("unexpected EOF")
            else -> states.removeLast().also { states.addLast(it + 1) }
        }
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (states.isEmpty()) fail("current not in a structure")
        return if (states.last() == -1) {
            decodeMapIndex(descriptor)
        } else {
            decodeListIndex(descriptor)
        }
    }


    override fun endStructure(descriptor: SerialDescriptor) {
        states.removeLast()
        if (alreadyReadEnd) {
            alreadyReadEnd = false
            return
        }
        skipStructured(1)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        when (lexer.next()) {
            is Token.DictStart -> states.addLast(-1)
            is Token.ListStart -> states.addLast(0)
            else -> fail("expect a list or dict")
        }
        return this
    }

    override fun fail(reason: String): Nothing = throw BencodingDecodeException(reason)

    private fun skipStructured(n: Int) {
        var counter = n
        while (counter > 0) {
            when (lexer.next()) {
                is Token.End -> counter--
                is Token.DictStart, Token.ListStart -> counter++
                is Token.EOF -> fail("unexpected EOF")
                is Token.Segment -> Unit
            }
        }
    }

    private fun skipValue() {
        when (lexer.next()) {
            is Token.Segment, is Token.EOF -> return
            else -> Unit
        }
        skipStructured(1)
    }
}


private fun unreachable(): Nothing = error("unreachable")
