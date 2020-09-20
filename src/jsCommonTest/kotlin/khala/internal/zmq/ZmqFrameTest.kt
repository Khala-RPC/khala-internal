package khala.internal.zmq

import khala.internal.forAllProtocols
import khala.internal.runTest
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.rawSocket
import kotlinx.coroutines.CompletableDeferred
import kotlin.test.Test

class ZmqFrameTest {

    @Test
    fun testBuffer() = runTest {
        forAllProtocols(port = 22700) { bindAddress, connectAddress ->
            val sock1 = ZmqContext.createAndBindDealer(bindAddress)
            val sock2 = ZmqContext.createAndConnectDealer(connectAddress)
            val result = CompletableDeferred<Unit>()
            sock1.rawSocket.on("message") { buffer ->
                result.complete(Unit)
            }
            sock2.rawSocket.send(arrayOf("1"))
            result.await()
            sock1.close()
            sock2.close()
        }

    }

}