package space.iseki.bencoding.internal

internal interface BWriter {
    fun writeData(b: Int)
    fun writeData(b: ByteArray)

    fun writeByteArray(s: ByteArray) {
        writeData(s.size.toString().encodeToByteArray())
        writeData(':'.code)
        writeData(s)
    }

    fun writeEnd() {
        writeData('e'.code)
    }

    fun getByteArray(): ByteArray = throw UnsupportedOperationException("Not implemented")

    fun writeDictBegin() {
        writeData('d'.code)
    }

    fun writeListBegin() {
        writeData('l'.code)
    }

    fun writeInt(i: Long) {
        writeData('i'.code)
        writeData(i.toString().encodeToByteArray())
        writeEnd()
    }

    fun writeString(s: String) {
        writeByteArray(s.encodeToByteArray())
    }
}
