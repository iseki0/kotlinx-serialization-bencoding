package space.iseki.bencoding

import kotlin.test.Test
import kotlin.test.assertEquals


class InputStreamITest {
    @Test
    fun test() {
        val input = "ldei1234ei-1234ei0e0:0:1:a4:abcdi0e".byteInputStream().let(::InputStreamI)
        assertEquals(Symbol.List, input.lookahead())
        assertEquals(Symbol.List, input.lookahead())
        assertEquals(Symbol.List, input.lookahead())
        input.skip()
        assertEquals(Symbol.Dict, input.lookahead())
        input.skip()
        assertEquals(Symbol.End, input.lookahead())
        input.skip()
        assertEquals(Symbol.Integer, input.lookahead())
        assertEquals(1234L, input.readNumber())
        assertEquals(-1234L, input.readNumber())
        assertEquals(0, input.readNumber())
        assertEquals("", input.readText().decodeToString())
        input.skip()
        assertEquals("a", input.readText().decodeToString())
        assertEquals("abcd", input.readText().decodeToString())
        input.skip()
        assertEquals(Symbol.EOF, input.lookahead())
        assertEquals(Symbol.EOF, input.lookahead())
    }
}
