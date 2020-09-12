package khala.internal.zmq

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import khala.internal.checkForEvent
import khala.internal.runTest
import khala.internal.zmq.*
import khala.internal.zmq.forAllProtocols
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.*

class ZmqLoopTest {

    @Test
    fun testOneLoopBasicMessaging() = runTest {
        forAllProtocols(port = 22800) { bindAddress, connectAddress ->
            val result = AtomicReference<ZmqMsg?>(null)
            val ctx = ZmqContext()
            val loop = ZmqLoop(
                context = ctx,
                userStateProducer = {},
                forwardListener = { address, msg ->
                    println("Received msg on dealer")
                    result.set(msg)
                },
                backwardListener = { msg ->
                    println("Received msg on router")
                    val identity = msg.popBytes()
                    val data = msg.popString()
                    assertFails { msg.popBytes() }
                    msg.addBytes(identity)
                    msg.addString(data.reversed())
                    println("Sending msg from router to dealer")
                    msg.send(backwardSocket!!)
                },
                backwardRouterBindAddress = bindAddress
            )
            loop.invokeSafe {
                sendForward(connectAddress) {
                    println("Sending from dealer to router")
                    +"privet"
                }
            }
            delay(200L)
            loop.stopSafe()
            val receivedMsg = result.value
            assertNotNull(receivedMsg)
            assertEquals("tevirp", receivedMsg.popString())
            assertFails { receivedMsg.popBytes() }
            receivedMsg.close()
        }
    }

}