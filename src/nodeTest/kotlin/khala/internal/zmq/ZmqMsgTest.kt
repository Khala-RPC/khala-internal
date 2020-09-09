package khala.internal.zmq

import kotlin.test.Test
import kotlin.test.assertEquals

internal fun twoSockets(port: Int = 0): Pair<ZmqSocket, ZmqSocket> {
    val ctx = ZmqContext()
    val sock1 = ctx.createAndBindDealer("ws://localhost:80")
    val sock2 = ctx.createAndConnectDealer("ws://localhost:80")
    return Pair(sock1, sock2)
}

class ZmqMsgTest {

    @Test
    fun testBasicBindConnect() {
        val (sock1, sock2) = twoSockets()
        sock2.close()
        sock1.close()
    }

    @Test
    fun testBasicMessaging() {
        val (sock1, sock2) = twoSockets()
        sock1.socket.on("message") { msg ->
            assertEquals("12345", msg.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send("12345")
    }

    @Test
    fun testMultiMessaging() {
        val (sock1, sock2) = twoSockets(1234)
        sock1.socket.on("message") { msgpart1, msgpart2 ->
            assertEquals("1234", msgpart1.toString())
            assertEquals("5678", msgpart2.toString())
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1234", "5678"))
    }

    @Test
    fun testEncoding() {
        val kt = "wss"
        console.log(kotlinStrToJsStr(kt))
    }
}