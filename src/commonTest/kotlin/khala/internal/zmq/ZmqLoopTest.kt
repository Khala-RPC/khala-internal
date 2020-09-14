package khala.internal.zmq

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import khala.internal.forAllProtocols
import khala.internal.runTest
import khala.internal.waitForCondition
import khala.internal.zmq.client.ClientLoop
import khala.internal.zmq.client.sendForward
import khala.internal.zmq.server.ServerLoop
import khala.internal.zmq.server.sendBackward
import kotlin.test.*

class ZmqLoopTest {

    @Test
    fun testInterLoopMultipartMessaging() = runTest {
        forAllProtocols(port = 22801) { bindAddress, connectAddress ->
            val answer = AtomicReference<String?>(null)
            val clientLoop = ClientLoop(
                loopStateProducer = {},
                socketStateProducer = {},
                forwardListener = { _, _, _, _, msg ->
                    answer.value = msg.popString() + msg.popString() + msg.popString()
                }
            )
            val serverLoop = ServerLoop(
                loopStateProducer = {},
                backwardListener = { _, msg ->
                    val identity = msg.popBytes()
                    val strKu = msg.popString().take(2)
                    val strAnd = "-"
                    val strPriff = msg.popString().take(5)
                    msg.close()
                    sendBackward {
                        +identity
                        +strKu
                        +strAnd
                        +strPriff
                    }
                },
                backwardRouterBindAddress = bindAddress
            )
            clientLoop.invokeSafe {
                sendForward(connectAddress) {
                    +"kuliti"
                    +"priffki"
                }
            }
            waitForCondition(500) { answer.value != null }
            clientLoop.stopSafe()
            serverLoop.stopSafe()
            val receivedStr = answer.get()
            assertNotNull(receivedStr)
            assertEquals("ku-priff", receivedStr)
        }
    }
}