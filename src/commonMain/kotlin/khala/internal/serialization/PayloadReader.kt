package khala.internal.serialization

import khala.internal.events.functions.RemoteFunctionLink
import khala.internal.events.functions.TheirFunctionLink
import khala.internal.events.streams.ReceivingStream
import khala.internal.events.streams.StreamMode
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
abstract class PayloadReader internal constructor(
    protected val bytes: ByteArray
) {

    protected fun getTheirFunction(theirFunctionID: Int): RemoteFunctionLink {
        //return TheirFunctionLink()
        TODO()
    }

    protected fun getReceivingStream(streamID: Int, streamMode: StreamMode): ReceivingStream {
        //return when (streamMode) { .... }
        TODO()
    }

}