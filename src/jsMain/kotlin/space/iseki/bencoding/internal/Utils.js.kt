package space.iseki.bencoding.internal

import kotlin.math.max

internal actual fun bytes2StringIso88591(bytes: ByteArray, off: Int, len: Int): String? = null

internal actual fun bytes2Long(bytes: ByteArray, off: Int, len: Int): Long {
    return bytes.copyOfRange(off, off + len).decodeToString().toLong()
}

internal actual fun createBytesWriter0(): BWriter = object : BWriter {
    private var pos = 0
    private fun ensureSize(delta: Int) {
        val newLen = pos + delta
        if (newLen < ba.size) {
            return
        }
        val target = ByteArray(newLength(ba.size, delta, ba.size))
        ba.copyInto(target)
        ba = target
    }

    private var ba = ByteArray(32)
    override fun writeData(b: Int) {
        ensureSize(1)
        ba[pos++] = b.toByte()
    }

    override fun writeData(b: ByteArray) {
        ensureSize(b.size)
        b.copyInto(ba, pos)
        pos += b.size
    }

    override fun getByteArray(): ByteArray {
        return ba.sliceArray(0 until pos)
    }
}

private fun newLength(oldLength: Int, minGrowth: Int, prefGrowth: Int): Int {
    val prefLength = (oldLength + max(minGrowth, prefGrowth)) // might overflow
    return if (0 < prefLength && prefLength <= Int.MAX_VALUE - 8) {
        prefLength
    } else {
        val minLength = oldLength + minGrowth
        return if (minLength < 0) { // overflow
            throw Error("Required array length $oldLength + $minGrowth is too large")
        } else if (minLength <= Int.MAX_VALUE - 8) {
            Int.MAX_VALUE - 8
        } else {
            minLength
        }
    }
}
