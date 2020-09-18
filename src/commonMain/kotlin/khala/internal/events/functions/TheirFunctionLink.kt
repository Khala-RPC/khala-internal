package khala.internal.events.functions

import khala.internal.serialization.PayloadReader
import khala.internal.serialization.PayloadWriter
import khala.internal.zmq.bindings.BinaryData

/**
 * Link to an anonymous function defined on connected client.
 * This class is internal, bindings should use only [RemoteFunctionLink] interface for received function arguments.
 */
internal class TheirFunctionLink(
    val clientIdentity: BinaryData,
    val theirQueryID: Long,
    val theirFunctionID: Int
) : RemoteFunctionLink {

    override fun invoke(
        invoker: (PayloadWriter) -> ((PayloadReader) -> Unit, (String) -> Unit) -> () -> Unit
    ) {
        TODO("Not yet implemented")
    }
    
}