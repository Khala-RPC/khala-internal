package khala.internal.zmq

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import khala.internal.runTest
import khala.internal.waitForCondition
import kotlin.test.*

class ZmqLoopTest {

    @Test
    fun testOneLoopBasicMessaging() = runTest {
        forAllProtocols(port = 22800) { bindAddress, connectAddress ->
            val lol = AtomicReference<String?>(null)
            val ctx = ZmqContext()

            val loop = ZmqLoop(
                context = ctx,
                userStateProducer = {},
                forwardListener = { address, msg ->
                    println("Received msg on dealer")
                    lol.value = msg.popString()
                },
                backwardListener = { msg ->
                    println("Received msg on router")
                    val identity = msg.popBytes()
                    val data = msg.popString()
                    msg.addBytes(identity)
                    msg.addString(data.reversed())
                    println("Sending msg from router to dealer")
                    msg.send(backwardSocket!!)
                },
                backwardRouterBindAddress = bindAddress
            )
            loop.invokeSafe {
                println("Invoking block")
                sendForward(connectAddress) {
                    println("Sending from dealer to router")
                    +"privet"
                }
            }
            waitForCondition(500) { lol.get() != null }
            val receivedMsg = lol.get()
            println("Deferred completed with $receivedMsg")
            loop.stopSafe()
            assertNotNull(receivedMsg)
            assertEquals("tevirp", receivedMsg)
        }
    }
}