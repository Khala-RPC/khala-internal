package khala.internal.zmq

import kotlin.test.Test
import kotlin.test.assertEquals

class ZmqFrameTest {

    @Test
    fun testBufferTCP() {
        val (sock1, sock2) = twoSockets("tcp")
        sock1.socket.on("message") { buffer ->
            console.log(buffer)
            val b = buffer as Buffer
            console.log(b)
            console.log(Buffer::class)
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1"))
    }


    @Test
    fun testBufferWS() {
        val (sock1, sock2) = twoSockets("ws")
        sock1.socket.on("message") { buffer ->
            console.log(buffer)
            val b = buffer as Buffer
            console.log(b)
            console.log(Buffer::class)
            sock1.close()
            sock2.close()
        }
        sock2.socket.send(arrayOf("1"))
    }
}