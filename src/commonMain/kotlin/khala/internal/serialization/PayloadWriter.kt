package khala.internal.serialization

import khala.internal.events.client.LocalFunction

abstract class PayloadWriter {

    private val _localFunctions = ArrayList<LocalFunction>()

    internal val localFunctions: List<LocalFunction> = _localFunctions

    /** Adds local function to the payload and returns its ID */
    protected fun addLocalFunction(localFunction: LocalFunction): Int {
        _localFunctions.add(localFunction)
        return _localFunctions.lastIndex
    }

    abstract fun buildBinary(): ByteArray

}