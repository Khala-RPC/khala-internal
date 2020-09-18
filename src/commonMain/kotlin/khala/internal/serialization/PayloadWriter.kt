package khala.internal.serialization

import khala.internal.events.functions.LocalFunction
import khala.internal.events.streams.DEFAULT
import khala.internal.events.streams.SendingStream
import khala.internal.events.streams.StreamMode
import khala.internal.events.streams.default.DefaultSendingStream

abstract class PayloadWriter {

    private val _localFunctions = ArrayList<LocalFunction>()
    internal val localFunctions: List<LocalFunction> = _localFunctions

    /** Puts local function to the payload and returns its ID */
    protected fun putLocalFunction(localFunction: LocalFunction): Int {
        _localFunctions += localFunction
        return _localFunctions.lastIndex
    }

    private val _sendingStreams = ArrayList<SendingStream>()
    internal val sendingStreams: List<SendingStream> = _sendingStreams

    /** Puts sending stream to the payload and returns its ID */
    protected fun putSendingStream(streamMode: StreamMode = DEFAULT): Int {
        val stream = when (streamMode) {
            DEFAULT -> DefaultSendingStream()
            else -> error("Stream mode $streamMode is not supported.")
        }
        _sendingStreams += stream
        return _sendingStreams.lastIndex
    }

    abstract fun buildBinary(): ByteArray

}