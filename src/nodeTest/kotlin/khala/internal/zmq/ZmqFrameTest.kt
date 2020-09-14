package khala.internal.zmq

import khala.internal.twoDealers
import kotlin.test.Test

class NodeZmqFrameTest {

    @Test
    fun testBufferTCP() {
        val (sock1, sock2) = twoDealers("tcp")
        sock1.socket.on("message") { buffer ->
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1"))
    }


    @Test
    fun testBufferWS() {
        val (sock1, sock2) = twoDealers("ws")
        sock1.socket.on("message") { buffer ->
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1"))
    }
}