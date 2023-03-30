package space.iseki.bencoding

internal sealed interface Token {
    object ListStart : Token {
        override fun toString(): String = "LIST_START"
    }

    object DictStart : Token {
        override fun toString(): String = "DICT_START"
    } // pre-End

    object End : Token {
        override fun toString(): String = "END"
    }

    object EOF : Token {
        override fun toString(): String = "EOF"
    }

    open class Segment(val data: ByteArray) : Token {
        override fun toString(): String = "SEGMENT:" + data.decodeToString()

        companion object : Segment(byteArrayOf()) {
            override fun toString(): String = "SEGMENT"
        }
    }
}
