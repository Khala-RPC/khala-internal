package khala.internal.events.streams

import khala.internal.serialization.PayloadReader
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport
@JsExport
interface ReceivingStream {

    fun subscribe(
        onNextElement: (PayloadReader) -> Unit,
        onCompleted: (PayloadReader) -> Unit,
        onError: (String) -> Unit
    )

}