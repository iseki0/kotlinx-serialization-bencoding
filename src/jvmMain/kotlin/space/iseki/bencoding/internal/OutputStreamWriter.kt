package space.iseki.bencoding.internal

import java.io.OutputStream

internal class OutputStreamWriter(private val output: OutputStream) : BWriter {
    override fun writeData(b: Int) {
        output.write(b)
    }

    override fun writeData(b: ByteArray) {
        output.write(b)
    }
}
