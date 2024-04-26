package space.iseki.bencoding

import java.io.BufferedInputStream
import java.io.EOFException
import java.io.InputStream

internal class InputStreamLexer(input: InputStream) : CommonLexer() {
    private val input = if (input.markSupported()) input else BufferedInputStream(input)
    private var pos = 0L
    override fun peek(): Int {
        input.mark(1)
        return input.read().also { input.reset() }
    }

    override fun readNumber(): Long {
        input.mark(20)
        val data = input.readNBytes(20)
        val i = firstNonDigit(data)
        val l: Long
        try {
            l = String(data, 0, i).toLong()
        } catch (e: NumberFormatException) {
            decodeError("invalid number")
        } finally {
            input.reset()
        }
        pos += i
        input.skipNBytes(i.toLong())
        return l
    }

    private fun firstNonDigit(data: ByteArray): Int {
        for (i in data.indices) {
            when {
                data[i] == '-'.code.toByte() -> continue
                data[i] < '0'.code.toByte() -> return i
                data[i] > '9'.code.toByte() -> return i
            }
        }
        return data.size
    }

    override fun readAtLeast(n: Int): ByteArray = input.readNBytes(n).also {
        if (it.size < n) decodeError("unexpected EOF")
        pos += n
    }

    override fun skip(n: Int) {
        try {
            input.skipNBytes(n.toLong())
        }catch (e: EOFException){
            decodeError("unexpected EOF")
        }
        pos += n
    }

    override fun pos(): Long = pos
}
