package khala.internal.zmq


import kotlin.test.Test
import kotlin.test.assertEquals

internal fun twoSockets(protocol: String): Pair<ZmqSocket, ZmqSocket> {
    val ctx = ZmqContext()
    val sock1 = ctx.createAndBindDealer("$protocol://*:12345")
    val sock2 = ctx.createAndConnectDealer("$protocol://localhost:12345")
    return Pair(sock1, sock2)
}

class ZmqSocketTest {

    @Test
    fun testBasicBindConnectTCP() {
        val (sock1, sock2) = twoSockets("tcp")
        sock2.close()
        sock1.close()
    }

    @Test
    fun testBasicBindConnectWS() {
        val (sock1, sock2) = twoSockets("ws")
        sock2.close()
        sock1.close()
    }

    @Test
    fun testBasicMessagingTCP() {
        val (sock1, sock2) = twoSockets("tcp")
        sock1.socket.on("message") { msg ->
            assertEquals("12345", msg.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send("12345")
    }

    @Test
    fun testBasicMessagingWS() {
        val (sock1, sock2) = twoSockets("ws")
        sock1.socket.on("message") { msg ->
            assertEquals("12345", msg.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send("12345")
    }

    @Test
    fun testMultiMessagingTCP() {
        val (sock1, sock2) = twoSockets("tcp")
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
        val (sock1, sock2) = twoSockets("ws")
        sock1.socket.on("message") { msgpart1, msgpart2 ->
            assertEquals("1234", msgpart1.toString())
            assertEquals("5678", msgpart2.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1234", "5678"))
    }

}