package khala.internal.events.client

import khala.internal.serialization.PayloadReader
import khala.internal.serialization.PayloadWriter
import khala.internal.zmq.client.ClientLoop
import khala.internal.zmq.client.ClientLoopScope

/**
 * Internal Client API for Khala bindings.
 */
object KhalaClient {

    private val clientLoop = ClientLoop(
        loopStateProducer = ::produceClientState,
        socketStateProducer = ::produceClientSocketState,
        forwardListener = ClientLoopScope<ClientState, ClientSocketState>::forwardListener
    )

    fun invokeRemoteFunction(
        address: String,
        name: String,
        arg: PayloadWriter,
        onResult: (PayloadReader) -> Unit,
        onError: (String) -> Unit
    ) {

    }

}