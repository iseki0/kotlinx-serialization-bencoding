package space.iseki.bencoding

internal interface BWriter {
    fun writeByte(b: Int)
    fun writeBytes(b: ByteArray)
    fun writeEnd() {
        writeByte('e'.code)
    }

    fun getByteArray(): ByteArray = throw UnsupportedOperationException("Not implemented")

    fun writeDictBegin() {
        writeByte('d'.code)
    }

    fun writeListBegin() {
        writeByte('l'.code)
    }

    fun writeInt(i: Long) {
        writeByte('i'.code)
        writeBytes(i.toString().encodeToByteArray())
        writeEnd()
    }

    fun writeString(s: String) {
        writeBytes(s.length.toString().encodeToByteArray())
        writeByte(':'.code)
        writeBytes(s.encodeToByteArray())
    }
}
