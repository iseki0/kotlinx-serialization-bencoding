package space.iseki.bencoding

import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlin.jvm.JvmStatic

class BencodeEncodeException(reason: String) : SerializationException(messageOf(reason)) {
    companion object {
        private fun messageOf(reason: String) = "encoding failed: $reason"
        private fun messageOf(reason: String, descriptor: SerialDescriptor, index: Int) =
            "encoding failed at $descriptor[$index]: $reason"

        @JvmStatic
        internal fun of(reason: String, descriptor: SerialDescriptor, index: Int) =
            BencodeEncodeException(messageOf(reason, descriptor, index))
    }
}
