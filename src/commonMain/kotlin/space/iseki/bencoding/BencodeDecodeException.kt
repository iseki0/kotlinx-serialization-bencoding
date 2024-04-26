package space.iseki.bencoding

import kotlinx.serialization.SerializationException

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class BencodeDecodeException(val pos: Long = -1L, reason: String) : SerializationException(messageOf(pos, reason)) {
    companion object {
        private fun messageOf(pos: Long, reason: String) = when {
            pos < 0 -> "decoding failed: $reason"
            else -> "decoding failed at position: $pos, $reason"
        }
    }
}