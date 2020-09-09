package khala.internal.zmq

import kotlin.test.Test
import kotlin.test.assertEquals

class ZmqMsgTest {

    @Test
    fun testMsgReceive() {
        val (sock1, sock2) = twoSockets("tcp")
        sock1.socket.on("message") { t1, t2 ->
            assertEquals("1", t1.toString())
            assertEquals("2", t2.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1", "2"))
    }

    @Test
    fun testJsFunction() {
        val foo = js("function(x, y) { return x + y; }")
        assertEquals(3, foo(1, 2))
    }

    @Test
    fun testVarargJsFunction() {
        val foo = js("function(f) { return function() { return f(arguments); }; }")
        val bar = foo { args ->
            assertEquals(123, args[0])
            assertEquals(3, args.length)
        }
        bar(123, 456, 789)
    }

}