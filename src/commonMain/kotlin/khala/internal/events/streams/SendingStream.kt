package khala.internal.events.streams

import khala.internal.serialization.PayloadWriter
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Asynchronous sending stream.
 * Stream may send any number of elements through [sendNextElement].
 * Once either [sendCompleted] or [sendError] is called, stream cannot send anything more.
 * Stream is considered completed when ALL following conditions happen:
 *  - a) Either [sendCompleted] or [sendError] is called.
 *  - b) All elements sent by [sendNextElement] are completed.
 *  - c) If [sendCompleted] was called, element sent by this method is completed.
 * Payload is considered completed, when all streams that were added to it are completed.
 * Payload that does not contain any streams is completed by default.
 * Once stream is completed, all memory resources consumed by this stream are released on both client and server.
 */
@ExperimentalJsExport
@JsExport
interface SendingStream {

    fun sendNextElement(elementWriter: (PayloadWriter) -> Unit)

    fun sendCompleted(completeWriter: (PayloadWriter) -> Unit)

    fun sendError(error: String)

}