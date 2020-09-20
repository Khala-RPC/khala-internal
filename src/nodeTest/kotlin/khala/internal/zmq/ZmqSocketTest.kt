package khala.internal.zmq


import khala.internal.runTest
import khala.internal.twoDealers
import khala.internal.zmq.bindings.Buffer
import khala.internal.zmq.bindings.bufferToString
import kotlinx.coroutines.CompletableDeferred
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeZmqSocketTest {

    @Test
    fun testBasicBindConnectTCP() {
        val (sock1, sock2) = twoDealers("tcp")
        sock2.close()
        sock1.close()
    }

    @Test
    fun testBasicBindConnectWS() {
        val (sock1, sock2) = twoDealers("ws")
        sock2.close()
        sock1.close()
    }

    @Test
    fun testBasicMessagingTCP() = runTest {
        val resultDef = CompletableDeferred<Buffer>()
        val (sock1, sock2) = twoDealers("tcp")
        sock1.socket.on("message") { msg ->
            resultDef.complete(msg)
        }
        sock2.socket.send("12345")
        val msg = resultDef.await()
        assertEquals("12345", msg.bufferToString())
        sock1.close()
        sock2.close()
    }

    @Test
    fun testBasicMessagingWS() = runTest {
        val resultDef = CompletableDeferred<Buffer>()
        val (sock1, sock2) = twoDealers("ws")
        sock1.socket.on("message") { msg ->
            resultDef.complete(msg)
        }
        sock2.socket.send("12345")
        val msg = resultDef.await()
        assertEquals("12345", msg.bufferToString())
        sock1.close()
        sock2.close()
    }

    @Test
    fun testMultiMessagingTCP() = runTest {
        val resultDef = CompletableDeferred<Pair<Buffer, Buffer>>()
        val (sock1, sock2) = twoDealers("tcp")
        sock1.socket.on("message") { msgpart1, msgpart2 ->
            resultDef.complete((msgpart1 as Buffer) to msgpart2)
        }
        sock2.socket.send(arrayOf("1234", "5678"))
        val (msgpart1, msgpart2) = resultDef.await()
        assertEquals("1234", msgpart1.bufferToString())
        assertEquals("5678", msgpart2.bufferToString())
        sock1.close()
        sock2.close()
    }

    @Test
    fun testMultiMessagingWS() = runTest {
        val resultDef = CompletableDeferred<Pair<Buffer, Buffer>>()
        val (sock1, sock2) = twoDealers("ws")
        sock1.socket.on("message") { msgpart1, msgpart2 ->
            resultDef.complete((msgpart1 as Buffer) to msgpart2)
        }
        sock2.socket.send(arrayOf("1234", "5678"))
        val (msgpart1, msgpart2) = resultDef.await()
        assertEquals("1234", msgpart1.bufferToString())
        assertEquals("5678", msgpart2.bufferToString())
        sock1.close()
        sock2.close()
    }

}