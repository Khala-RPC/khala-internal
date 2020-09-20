package khala.internal.zmq

import khala.internal.runTest
import khala.internal.twoDealers
import khala.internal.zmq.bindings.Buffer
import khala.internal.zmq.bindings.ZmqMsg
import khala.internal.zmq.bindings.bufferToString
import kotlinx.coroutines.CompletableDeferred
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeZmqMsgTest {

    @Test
    fun testMsgReceive() = runTest {
        val (sock1, sock2) = twoDealers("tcp")
        val resultDef = CompletableDeferred<Pair<dynamic, dynamic>>()
        sock1.socket.on("message") { t1, t2 ->
            resultDef.complete((t1 as Buffer).bufferToString() to (t2 as Buffer).bufferToString())
        }
        sock2.socket.send(arrayOf("1", "2"))
        val (t1, t2) = resultDef.await()
        assertEquals("1", t1.toString())
        assertEquals("2", t2.toString())
        sock1.close()
        sock2.close()
    }

    @Test
    fun testJsFunction() {
        val foo = js("function(x, y) { return x + y; }")
        assertEquals(3, foo(1, 2))
    }

    @Test
    fun testVarargJsFunctionReceive() {
        val foo = js("function(f) { return function() { return f(arguments); }; }")
        val bar = foo { args ->
            assertEquals(123, args[0])
            assertEquals(3, args.length)
        }
        bar(123, 456, 789)
    }

    @Test
    fun testVarargJsFunctionSend() {
        val sendFun = js("function() { console.log(arguments); }")
        val foo = js("function(sendFun) { return function(arr) { sendFun.apply(this, arr); }; }")
        val bar = foo(sendFun)
        bar(arrayOf(123, 456, 789))
    }

    @Test
    fun testBuffer() {
        val msg = ZmqMsg()
        msg.addString("rofl")
        val s = msg.popString()
        println(s)
    }

}