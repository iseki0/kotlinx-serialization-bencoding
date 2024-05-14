package space.iseki.bencoding

internal actual fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long = String(bytes, off, len).toLong()
internal actual fun bytes2StringIso88591(bytes: ByteArray, off: Int, len: Int): String =
    String(bytes, off, len, Charsets.ISO_8859_1)
internal actual fun string2BytesIso88591(s: String): ByteArray = s.toByteArray(Charsets.ISO_8859_1)
