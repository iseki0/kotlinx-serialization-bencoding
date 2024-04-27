package space.iseki.bencoding

internal expect fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long
internal expect fun bytes2StringIso88591(bytes: ByteArray, off: Int = 0, len: Int = bytes.size): String
internal class BytesLexer(private val bytes: ByteArray) : CommonLexer() {
    private var pos = 0
    override fun peek(): Int {
        if (pos >= bytes.size) return Lexer.EOF
        return bytes[pos].toInt()
    }


    override fun readNumber(): Long {
        var i = pos
        while (i < bytes.size && (bytes[i] in '0'.code..'9'.code || bytes[i] == '-'.code.toByte())) i++
        if (i == pos) decodeError("invalid number")
        try {
            return bytes2Long(bytes, pos, i - pos)
        } catch (e: NumberFormatException) {
            decodeError("invalid number")
        } finally {
            pos = i
        }
    }

    override fun readAtLeast(n: Int): ByteArray = bytes.copyOfRange(pos, pos + n).also {
        if (it.size < n) decodeError("unexpected EOF")
        pos += n
    }

    override fun skip(n: Int) {
        pos += n
    }

    override fun pos(): Long = pos.toLong()
}
