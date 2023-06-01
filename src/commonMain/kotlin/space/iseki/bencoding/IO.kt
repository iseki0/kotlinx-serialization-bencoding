package space.iseki.bencoding

internal enum class Symbol {
    List,
    Dict,
    Integer,
    Text,
    End,
    EOF,
}

internal interface I {
    val pos: Int
    fun lookahead(): Symbol
    fun readText(): ByteArray
    fun readNumber(): Long
    fun skip()
}
