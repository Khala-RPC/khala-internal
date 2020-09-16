package khala.internal.serialization

import khala.internal.events.functions.LocalFunction
import khala.internal.events.streams.SendingStream

abstract class PayloadWriter {

    private val _localFunctions = ArrayList<LocalFunction>()
    internal val localFunctions: List<LocalFunction> = _localFunctions

    /** Adds local function to the payload and returns its ID */
    protected fun addLocalFunction(localFunction: LocalFunction): Int {
        _localFunctions.add(localFunction)
        return _localFunctions.lastIndex
    }

    private val _sendingStreams = ArrayList<SendingStream>()
    internal val sendingStreams: List<SendingStream> = _sendingStreams

    protected fun addSendingStream(sendingStream: SendingStream): Int {

    }

    abstract fun buildBinary(): ByteArray

}