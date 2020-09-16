package khala.internal.events.functions

import khala.internal.events.client.KhalaClient
import khala.internal.serialization.PayloadReader
import khala.internal.serialization.PayloadWriter

/**
 * Link to named remote function which is publicly available to all queries.
 */
class RemoteFunctionLink(val address: String, val name: String) : FunctionLink {

    override fun invoke(
        invoker: (PayloadWriter) -> ((PayloadReader) -> Unit, (String) -> Unit) -> () -> Unit
    ) {
        return KhalaClient.invokeRemoteFunction(address, name, invoker)
    }

}