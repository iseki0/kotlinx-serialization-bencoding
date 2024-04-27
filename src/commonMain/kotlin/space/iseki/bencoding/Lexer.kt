package space.iseki.bencoding

internal interface Lexer {
    companion object {
        const val EOF = -1
        const val END = 1
        const val DICT = 2
        const val LIST = 3
        const val STRING = 4
        const val INTEGER = 5
        private fun Lexer.doThrow(message: String): Nothing = throw BencodeDecodeException(pos(), message)

        fun name(i: Int) = when (i) {
            EOF -> "EOF"
            END -> "END"
            DICT -> "DICT"
            LIST -> "LIST"
            STRING -> "STRING"
            INTEGER -> "INTEGER"
            else -> "UNKNOWN"
        }
    }

    fun pos(): Long
    fun la(): Int
    fun nextInteger(): Long
    fun nextBytes(): ByteArray
    fun nextString(): String = nextBytes().decodeToString()
    fun skipToken()
    fun skipValue() {
        if (la() == END) return
        var depth = 0
        do {
            when (val la = la()) {
                EOF -> doThrow("unexpected EOF")
                END -> if (depth == 0) doThrow("unexpected END") else depth--
                LIST, DICT -> depth++
                STRING, INTEGER -> {}
                else -> throw Error("bad token = $la")
            }
            skipToken()
        } while (depth > 0)
    }
}