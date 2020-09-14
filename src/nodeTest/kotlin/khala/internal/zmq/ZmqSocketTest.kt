package khala.internal.zmq


import khala.internal.twoDealers
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
    fun testBasicMessagingTCP() {
        val (sock1, sock2) = twoDealers("tcp")
        sock1.socket.on("message") { msg ->
            assertEquals("12345", msg.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send("12345")
    }

    @Test
    fun testBasicMessagingWS() {
        val (sock1, sock2) = twoDealers("ws")
        sock1.socket.on("message") { msg ->
            assertEquals("12345", msg.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send("12345")
    }

    @Test
    fun testMultiMessagingTCP() {
        val (sock1, sock2) = twoDealers("tcp")
        sock1.socket.on("message") { msgpart1, msgpart2 ->
            assertEquals("1234", msgpart1.toString())
            assertEquals("5678", msgpart2.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1234", "5678"))
    }

    @Test
    fun testMultiMessagingWS() {
        val (sock1, sock2) = twoDealers("ws")
        sock1.socket.on("message") { msgpart1, msgpart2 ->
            assertEquals("1234", msgpart1.toString())
            assertEquals("5678", msgpart2.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1234", "5678"))
    }

}