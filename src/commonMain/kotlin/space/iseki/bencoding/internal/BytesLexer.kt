package space.iseki.bencoding.internal


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
            return bytes.copyOfRange(pos, i).decodeToString().toLong()
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
