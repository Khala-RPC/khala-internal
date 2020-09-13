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
            val answer = AtomicReference<String?>(null)
            val ctx = ZmqContext()

            val loop = ZmqLoop(
                context = ctx,
                userStateProducer = {},
                forwardListener = { address, msg ->
                    println("Received msg on dealer")
                    answer.value = msg.popString()
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
            waitForCondition(500) { answer.get() != null }
            val receivedMsg = answer.get()
            println("Deferred completed with $receivedMsg")
            loop.stopSafe()
            assertNotNull(receivedMsg)
            assertEquals("tevirp", receivedMsg)
        }
    }

    @Test
    fun testInterLoopBasicMessaging() = runTest {
        forAllProtocols(port = 22801) { bindAddress, connectAddress ->
            val answer = AtomicReference<String?>(null)
            val ctx = ZmqContext()
            val clientLoop = ZmqLoop(
                context = ctx,
                userStateProducer = {},
                forwardListener = { address, msg ->
                    answer.value = msg.popString() + msg.popString() + msg.popString()
                },
                backwardListener = {},
                backwardRouterBindAddress = null
            )
            val serverLoop = ZmqLoop(
                context = ctx,
                userStateProducer = {},
                forwardListener = { _, _ -> },
                backwardListener = { msg ->
                    val identity = msg.popBytes()
                    val strKu = msg.popString().take(2)
                    val strAnd = "-"
                    val strPriff = msg.popString().take(5)
                    msg.close()
                    sendMsg(backwardSocket!!) {
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