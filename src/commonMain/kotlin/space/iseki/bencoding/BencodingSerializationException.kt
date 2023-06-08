package space.iseki.bencoding

import kotlinx.serialization.SerializationException

open class BencodingSerializationException(
    override val message: String = "",
    override val cause: Throwable? = null,
) : SerializationException()

class BencodingDecodeException(
    val reason: String,
    val position: Long,
    cause: Throwable? = null,
) : BencodingSerializationException("decode failed at $position, $reason", cause)
