package space.iseki.bencoding

import kotlinx.serialization.SerializationException

class BencodeEncodeException(reason: String) : SerializationException(messageOf(reason)) {
    companion object {
        private fun messageOf(reason: String) = "encoding failed: $reason"
    }
}
