package space.iseki.bencoding

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import java.io.InputStream

private const val UNINITIALIZED = -2
private const val EOF = -1

internal class InputStreamI(private val inputStream: InputStream) : I {
    override var pos: Int = 0
        private set

    override fun lookahead(): Symbol = when (la()) {
        'l'.code -> Symbol.List
        'd'.code -> Symbol.Dict
        'i'.code -> Symbol.Integer
        in '0'.code..'9'.code -> Symbol.Text
        'e'.code -> Symbol.End
        EOF -> Symbol.EOF
        else -> unrecognizedInput()
    }

    override fun readText(): ByteArray = readLength().let { n -> inputStream.readNBytes(n).also { pos += n } }

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
            Symbol.Text -> readLength().let { n -> inputStream.skipNBytes(n.toLong()); pos += n }
            Symbol.Integer -> readNumber()
        }
    }

    private var _la = UNINITIALIZED

    private fun la() = when (_la) {
        UNINITIALIZED -> inputStream.read().also { _la = it;pos++ }
        else -> _la
    }

    private fun read() = when (_la) {
        UNINITIALIZED -> inputStream.read().also { pos++ }
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

    @Suppress("SameParameterValue")
    private fun error(msg: String): Nothing = throw BencodingSerializationException(msg)

}

inline fun <reified T> InputStream.decodeInBencoding() = decodeInBencoding(serializer<T>())

fun <T> InputStream.decodeInBencoding(serializer: KSerializer<T>) =
    BencodingDecoderImpl(InputStreamI(this)).decodeSerializableValue(serializer)
