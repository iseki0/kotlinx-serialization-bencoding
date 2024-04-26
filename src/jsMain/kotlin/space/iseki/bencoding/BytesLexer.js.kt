package space.iseki.bencoding

internal actual fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long =
    bytes.copyOfRange(off, off + len).decodeToString().toLong()