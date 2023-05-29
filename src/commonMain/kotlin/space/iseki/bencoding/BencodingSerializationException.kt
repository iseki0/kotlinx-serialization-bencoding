package space.iseki.bencoding

import kotlinx.serialization.SerializationException

class BencodingSerializationException(
    override val message: String = "",
    override val cause: Throwable? = null,
) : SerializationException()
