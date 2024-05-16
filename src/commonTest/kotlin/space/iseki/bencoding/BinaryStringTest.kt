package space.iseki.bencoding

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryStringTest {
    @Serializable
    data class A(
        @BinaryString val a: String,
    )

    @Serializable
    data class B(
        @BinaryString(BinaryStringStrategy.ISO88591) val a: String,
    )

    @Serializable
    data class C(
        @BinaryString(BinaryStringStrategy.Base64) val a: String,
    )

    @OptIn(ExperimentalEncodingApi::class)
    companion object {
        private val str = "\u0000\u0001\u0002"
        private val data = "d1:a3:${str}e".encodeToByteArray()

        init {
            check(data.size == 10)
        }

        private val binaryA = A(str)
        private val base64A = A(Base64.encode(str.encodeToByteArray()))
        private val binaryB = B(str)
        private val base64C = C(base64A.a)

        init {
            check(base64A.a.length == 4)
        }
    }

    @Test
    fun testDefault() {
        assertEquals(binaryA, Bencode.decodeFromByteArray<A>(data))
        assertEquals(
            base64A,
            Bencode { binaryStringStrategy = BinaryStringStrategy.Base64 }.decodeFromByteArray<A>(data)
        )
    }

    @Test
    fun testNonDefault(){
        assertEquals(binaryB, Bencode.decodeFromByteArray<B>(data))
        assertEquals(base64C, Bencode.decodeFromByteArray<C>(data))
    }
}
