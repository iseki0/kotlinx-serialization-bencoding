package space.iseki.bencoding

import kotlin.test.Test
import kotlin.test.assertTrue

class BencodingDecodeExceptionTest{

    @Test
    fun test(){
        val th =  checkNotNull(runCatching { throw BencodingDecodeException("test", 10) }.exceptionOrNull())
        assertTrue { th is BencodingDecodeException && th.position == 10L && th.reason == "test" }
    }
}
