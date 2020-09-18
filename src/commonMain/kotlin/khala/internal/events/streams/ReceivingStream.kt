package khala.internal.events.streams

import khala.internal.serialization.PayloadReader

interface ReceivingStream {

    fun subscribe(
        onNextElement: (PayloadReader) -> Unit,
        onCompleted: (PayloadReader) -> Unit,
        onError: (String) -> Unit
    )

}