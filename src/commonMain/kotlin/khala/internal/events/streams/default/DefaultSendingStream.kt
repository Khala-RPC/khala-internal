package khala.internal.events.streams.default

import khala.internal.events.streams.SendingStream
import khala.internal.serialization.PayloadWriter
import kotlin.js.ExperimentalJsExport

/**
 * Implementation of SendingStream with DEFAULT mode.
 * @see khala.internal.events.streams.DEFAULT
 */
@ExperimentalJsExport
internal class DefaultSendingStream : SendingStream {

    override fun sendNextElement(elementWriter: (PayloadWriter) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun sendCompleted(completeWriter: (PayloadWriter) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun sendError(error: String) {
        TODO("Not yet implemented")
    }

}