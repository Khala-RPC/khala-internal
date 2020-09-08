package khala.internal.zmq

import kotlin.test.Test

class ZmqMsgTest {

    @Test
    fun test() {
        println("LOL")
        val ctx = ZmqContext()
        val sock1 = ctx.createDealerSocket()
        val sock2 = ctx.createDealerSocket()
        println("a")
        sock1.bind("tcp://*:12345")
        println("b")
        sock2.connect("tcp://localhost:12345")
        println("c")
        sock2.close()
        println("d")
        sock1.close()
        println("e")
    }
}