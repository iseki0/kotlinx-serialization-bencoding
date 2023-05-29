package space.iseki.bencoding

import java.io.InputStream

private const val UNINITIALIZED = -2
private const val EOF = -1

internal class InputStreamI(private val inputStream: InputStream) : I {

    override fun lookahead(): Symbol = when (la()) {
        'l'.code -> Symbol.List
        'd'.code -> Symbol.Dict
        'i'.code -> Symbol.Integer
        in '0'.code..'9'.code -> Symbol.Text
        'e'.code -> Symbol.End
        EOF -> Symbol.EOF
        else -> unrecognizedInput()
    }

    override fun readText(): ByteArray = inputStream.readNBytes(readLength())

    override fun readNumber(): Long {
        var n = 0L
        var factor = 1
        if (read() != 'i'.code) unrecognizedInput("readNumber")
        while (true) {
            when (val i = read()) {
                '-'.code -> {
                    if (n != 0L || factor != 1) unrecognizedInput("readNumber")
                    factor = -1
                }

                in '0'.code..'9'.code -> {
                    n = n * 10 + (i - '0'.code) * factor
                }

                'e'.code -> break
                else -> unrecognizedInput("readNumber")
            }
        }
        return n
    }

    override fun skip() {
        when (lookahead()) {
            Symbol.EOF -> return
            Symbol.Dict, Symbol.List, Symbol.End -> read()
            Symbol.Text -> inputStream.skipNBytes(readLength().toLong())
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
                else -> unrecognizedInput("readLength")
            }
            if (l < 0) error("length overflow")
        }
        return l
    }

    private fun unrecognizedInput(during: String = ""): Nothing = when {
        during.isEmpty() -> "unrecognized input"
        else -> "unrecognized input, during $during"
    }.let { throw BencodingSerializationException(it) }

    private fun error(msg: String): Nothing = throw BencodingSerializationException(msg)

}

