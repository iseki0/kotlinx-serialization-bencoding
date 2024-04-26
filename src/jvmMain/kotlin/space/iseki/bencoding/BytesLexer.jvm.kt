package space.iseki.bencoding

internal actual fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long = String(bytes, off, len).toLong()
