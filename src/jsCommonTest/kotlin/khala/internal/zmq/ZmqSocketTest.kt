package khala.internal.zmq


import khala.internal.forAllDealerPairs
import khala.internal.runTest
import khala.internal.zmq.bindings.Buffer
import khala.internal.zmq.bindings.bufferToString
import khala.internal.zmq.bindings.rawSocket
import kotlinx.coroutines.CompletableDeferred
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeZmqSocketTest {

    @Test
    fun testBasicBindConnect() {
        forAllDealerPairs(port = 12345) { bound, connected ->
            bound.close()
            connected.close()
        }
    }

    @Test
    fun testBasicMessaging() = runTest {
        forAllDealerPairs(port = 22702) { sock1, sock2 ->
            val resultDef = CompletableDeferred<Buffer>()
            sock1.rawSocket.on("message") { msg ->
                resultDef.complete(msg)
            }
            sock2.rawSocket.send("12345")
            val msg = resultDef.await()
            assertEquals("12345", msg.bufferToString())
            sock1.close()
            sock2.close()
        }
    }

    @Test
    fun testMultiMessaging() = runTest {
        forAllDealerPairs(port = 22703) { sock1, sock2 ->
            val resultDef = CompletableDeferred<Pair<Buffer, Buffer>>()
            sock1.rawSocket.on("message") { msgpart1, msgpart2 ->
                resultDef.complete((msgpart1 as Buffer) to msgpart2)
            }
            sock2.rawSocket.send(arrayOf("1234", "5678"))
            val (msgpart1, msgpart2) = resultDef.await()
            assertEquals("1234", msgpart1.bufferToString())
            assertEquals("5678", msgpart2.bufferToString())
            sock1.close()
            sock2.close()
        }
    }

}