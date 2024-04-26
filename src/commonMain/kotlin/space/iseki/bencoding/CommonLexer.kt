package space.iseki.bencoding

internal abstract class CommonLexer : Lexer {
    private fun unexpected(expected: Int, actual: Int): Nothing {
        val exp = Lexer.name(expected)
        val act = Lexer.name(actual)
        decodeError("expect $exp, but got $act")
    }

    protected fun decodeError(message: String): Nothing = throw BencodeDecodeException(pos(), message)
    protected abstract fun peek(): Int
    protected abstract fun readNumber(): Long
    protected abstract fun readAtLeast(n: Int): ByteArray
    protected abstract fun skip(n: Int)
    protected fun readString(n: Int): String = readAtLeast(n).decodeToString()
    override fun la() = when (val b = peek()) {
        -1 -> Lexer.EOF
        'i'.code -> Lexer.INTEGER
        'l'.code -> Lexer.LIST
        'd'.code -> Lexer.DICT
        'e'.code -> Lexer.END
        in '0'.code..'9'.code -> Lexer.STRING
        else -> decodeError("unexpected byte: $b")
    }

    override fun nextInteger(): Long {
        if (la() != Lexer.INTEGER) unexpected(Lexer.INTEGER, la())
        skip(1)
        return readNumber().also {
            if (la() != Lexer.END) unexpected(Lexer.END, la())
            skip(1)
        }
    }

    override fun nextBytes(): ByteArray = readAtLeast(readStringLen())
    override fun nextString(): String = readString(readStringLen())

    override fun skipToken() {
        when (la()) {
            Lexer.INTEGER -> nextInteger()
            Lexer.STRING -> skip(readStringLen())
            Lexer.LIST, Lexer.DICT, Lexer.END -> skip(1)
            Lexer.EOF -> {}
            else -> error("bad token")
        }
    }

    private fun readStringLen(): Int {
        if (la() != Lexer.STRING) unexpected(Lexer.STRING, la())
        val lenL = readNumber()
        val len = lenL.toInt()
        if (len.toLong() != lenL) decodeError("length overflow")
        if (len < 0) decodeError("negative length")
        if (peek() != ':'.code) decodeError("expect ':'")
        skip(1)
        return len
    }
}