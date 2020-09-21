package khala.internal.events.client

import khala.internal.serialization.PayloadReader
import khala.internal.serialization.PayloadWriter
import khala.internal.zmq.client.ClientLoop
import khala.internal.zmq.client.ClientLoopScope
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.concurrent.SharedImmutable

/**
 * Internal Client API for Khala bindings.
 */
internal object KhalaClient {

    private val clientLoop = ClientLoop(
        loopStateProducer = ::produceClientState,
        socketStateProducer = ::produceClientSocketState,
        forwardListener = ClientLoopScope<ClientState, ClientSocketState>::forwardListener
    )

    @ExperimentalJsExport
    fun invokeRemoteFunction(
        address: String,
        name: String,
        invoker: (PayloadWriter) -> ((PayloadReader) -> Unit, (String) -> Unit) -> () -> Unit
    ) {
        TODO()
    }

}