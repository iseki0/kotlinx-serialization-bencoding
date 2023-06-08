package space.iseki.bencoding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import java.io.EOFException
import java.io.InputStream

private const val UNINITIALIZED = -2
private const val EOF = -1


internal class InputStreamI(inputStream: InputStream) : I {
    private val inputStream = CounteredInputStream(inputStream)
    override val pos: Long
        get() = inputStream.pos

    override fun lookahead(): Symbol = when (la()) {
        'l'.code -> Symbol.List
        'd'.code -> Symbol.Dict
        'i'.code -> Symbol.Integer
        in '0'.code..'9'.code -> Symbol.Text
        'e'.code -> Symbol.End
        EOF -> Symbol.EOF
        else -> unrecognizedInput()
    }

    override fun readText(): ByteArray {
        val len = readLength()
        if (len == 0) return ByteArray(0)
        val buffer = ByteArray(len)
        var p = 0
        while (p < buffer.size) {
            p = inputStream.read(buffer, p, buffer.size - p)
            if (p == -1) {
                unexpectedEOF("read $len bytes as text")
            }
        }
        return buffer
    }

    override fun readNumber(): Long {
        var n = 0L
        var factor = 1
        if (read() != 'i'.code) unrecognizedInput("read number")
        while (true) {
            when (val i = read()) {
                '-'.code -> {
                    if (n != 0L || factor != 1) unrecognizedInput("read number")
                    factor = -1
                }

                in '0'.code..'9'.code -> {
                    n = n * 10 + (i - '0'.code) * factor
                }

                'e'.code -> break
                EOF -> unexpectedEOF("read number")
                else -> unrecognizedInput("read number")
            }
        }
        return n
    }

    override fun skip() {
        when (lookahead()) {
            Symbol.EOF -> return
            Symbol.Dict, Symbol.List, Symbol.End -> read()
            Symbol.Text -> {
                val len = readLength()
                try {
                    inputStream.skipNBytes(len.toLong())
                } catch (ex: EOFException) {
                    unexpectedEOF("skip $len bytes")
                }
            }

            Symbol.Integer -> readNumber()
        }
    }

    private var _la = UNINITIALIZED

    private fun la() = when (_la) {
        UNINITIALIZED -> inputStream.read().also { _la = it }
        else -> _la
    }

    private fun read() = when (_la) {
        UNINITIALIZED -> inputStream.read()
        else -> _la.also { _la = UNINITIALIZED }
    }

    private fun readLength(): Int {
        var l = 0
        while (true) {
            when (val i = read()) {
                in '0'.code..'9'.code -> l = l * 10 + (i - '0'.code)
                ':'.code -> break
                EOF -> unexpectedEOF("read length")
                else -> unrecognizedInput("read length")
            }
            if (l < 0) error("length overflow")
        }

        return l
    }

    private fun unexpectedEOF(during: String = ""): Nothing = when {
        during.isEmpty() -> "unexpected EOF"
        else -> "unexpected EOF during $during"
    }.let { error(it) }

    private fun unrecognizedInput(during: String = ""): Nothing = when {
        during.isEmpty() -> "unrecognized input"
        else -> "unrecognized input, during $during"
    }.let { throw BencodingDecodeException(it, pos) }

    private fun error(reason: String, cause: Throwable? = null): Nothing =
        throw BencodingDecodeException(reason, pos, cause)

    private class CounteredInputStream(private val inputStream: InputStream) : InputStream() {
        var pos = 0L
            private set

        override fun read(): Int = inputStream.read().also { if (it > 0) pos += it }
        override fun skip(n: Long): Long = inputStream.skip(n).also { if (it > 0) pos += it }
        override fun read(b: ByteArray, off: Int, len: Int): Int =
            inputStream.read(b, off, len).also { if (it > 0) pos += it }
    }
}

