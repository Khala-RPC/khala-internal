package khala.internal.events.functions

import khala.internal.serialization.PayloadReader
import khala.internal.serialization.PayloadWriter
import khala.internal.zmq.bindings.ZmqBinaryData
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Link to an anonymous function defined on connected client.
 * This class is internal, bindings should use only [RemoteFunctionLink] interface for received function arguments.
 */
@ExperimentalJsExport
internal class TheirFunctionLink(
    val clientIdentity: ZmqBinaryData,
    val theirQueryID: Long,
    val theirFunctionID: Int
) : RemoteFunctionLink {

    override fun invoke(
        invoker: (PayloadWriter) -> ((PayloadReader) -> Unit, (String) -> Unit) -> () -> Unit
    ) {
        TODO("Not yet implemented")
    }
    
}