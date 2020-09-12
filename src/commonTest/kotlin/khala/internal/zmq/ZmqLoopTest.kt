package khala.internal.zmq

import co.touchlab.stately.concurrency.AtomicReference

class ZmqLoopTest {

    fun testOneLoopBasicMessaging() = forAllProtocols { protocol ->
        val result = AtomicReference<ZmqMsg?>(null)
        val ctx = ZmqContext()
        val loop = ZmqLoop(
                context = ctx,
                userStateProducer = {},
                forwardListener = { address, msg ->
                    result.set(msg)
                },
                backwardListener = { msg ->
                    val identity = msg.popBytes()
                    val data = msg.popString()
                    msg.addBytes(identity)
                    msg.addString(data.reversed())
                    msg.send(backwardSocket!!)
                },
                backwardRouterBindAddress = "$protocol://*:22800"
        )
        loop.invokeSafe {
            sendForward("$protocol://localhost:22800") {
                + "privet"
            }
        }

    }

}