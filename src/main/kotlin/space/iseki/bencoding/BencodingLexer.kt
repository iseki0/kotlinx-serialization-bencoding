package space.iseki.bencoding

internal class Lexer(input: Input) {
    private var buffered: Token? = null
    private val iter = input.tokenStream().iterator()
    fun next(): Token {
        buffered?.let {
            buffered = null
            return it
        }
        return iter.next()
    }

    fun la1(): Token {
        buffered?.let { return it }
        return iter.next().also { buffered = it }
    }
}

private fun Input.tokenStream() = sequence {
    while (true) {
        var ch = read()
        when (ch) {
            -1 -> yield(Token.EOF)
            'l'.code -> yield(Token.ListStart)
            'd'.code -> yield(Token.DictStart)
            'e'.code -> yield(Token.End)
            'i'.code -> {
                var buf = ByteArray(16)
                var ptr = 0
                while (true) {
                    ch = read()
                    if (ch == -1) fail("unexpected EOF")
                    if (ch == 'e'.code) {
                        break
                    }
                    if (ptr > buf.lastIndex) {
                        buf = buf.copyOf(buf.size + 32)
                    }
                    buf[ptr] = ch.toByte()
                    ptr++
                }
                yield(buf.copyOf(ptr).asSegment())
            }

            in '0'.code..'9'.code -> {
                // string
                var i = 0
                while (true) {
                    if (i < 0) fail("length overflow")
                    if (ch == -1) fail("unexpected EOF")
                    if (ch == ':'.code) break
                    val n = ch.toChar() - '0'
                    if (n !in 0..9) fail("expect a number")
                    i = i * 10 + n
                    ch = read()
                }
                yield(readN(i).asSegment())
            }

            else -> fail("unexpected character: ${ch.toChar()}")
        }
    }
}.map { it.also { debug(it) } }


private fun ByteArray.asSegment() = Token.Segment(this)

