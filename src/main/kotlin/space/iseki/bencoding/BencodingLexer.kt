package space.iseki.bencoding

internal class BencodingLexer(
    private val input: Input,
) {
    private var lastValue: Token = next()

    fun peek() = lastValue

    fun consume(): Token = lastValue.also { lastValue = next() }

    private fun next(): Token {
        var ch = input.read()
        return when (ch) {
            -1 -> Token.EOF
            'l'.code -> Token.ListStart
            'd'.code -> Token.DictStart
            'e'.code -> Token.End
            'i'.code -> {
                var buf = ByteArray(16)
                var ptr = 0
                while (true) {
                    ch = input.read()
                    if (ch == -1) input.fail("unexpected EOF")
                    if (ch == 'e'.code) {
                        break
                    }
                    if (ptr > buf.lastIndex) {
                        buf = buf.copyOf(buf.size + 32)
                    }
                    buf[ptr] = ch.toByte()
                    ptr++
                }
                buf.copyOf(ptr).asSegment()
            }

            in '0'.code..'9'.code -> {
                // string
                var i = 0
                while (true) {
                    if (i < 0) input.fail("length overflow")
                    if (ch == -1) input.fail("unexpected EOF")
                    if (ch == ':'.code) break
                    val n = ch.toChar() - '0'
                    if (n !in 0..9) input.fail("expect a number")
                    i = i * 10 + n
                    ch = input.read()
                }
                input.readN(i).asSegment()
            }

            else -> input.fail("unexpected character: ${ch.toChar()}")
        }.also { lastValue = it; debug(it) }
    }
}

private fun ByteArray.asSegment() = Token.Segment(this)

