package space.iseki.bencode

import java.io.InputStream

internal interface Input {
    fun read(): Int
    fun readN(n: Int): ByteArray
    fun fail(reason: String): Nothing
}

internal interface Output {
    fun write(ch: Char)
    fun write(data: ByteArray)
    fun flush()
}

internal class WrappedInputStream(private val inputStream: InputStream) : Input {
    private var pos = 0
    override fun read(): Int = inputStream.read().also { if (it > -1) pos++ }

    override fun readN(n: Int): ByteArray = inputStream.readNBytes(n).also { pos += it.size }

    override fun fail(reason: String): Nothing {
        throw BencodeDecodeException("$reason [pos: $pos]")
    }
}
