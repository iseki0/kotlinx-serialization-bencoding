package space.iseki.bencoding

import java.nio.charset.StandardCharsets

internal actual fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long = String(bytes, off, len).toLong()
internal actual fun bytes2StringIso88591(bytes: ByteArray, off: Int, len: Int): String =
    String(bytes, off, len, StandardCharsets.ISO_8859_1)

internal actual fun string2BytesIso88591(s: String): ByteArray = s.toByteArray(StandardCharsets.ISO_8859_1)
