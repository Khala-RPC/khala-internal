package khala.internal

import kotlinx.coroutines.delay

/** Protocols that will be tested. Browser JS does not allow binding zmq sockets, to it will not be tested at all :( */
internal expect val supportedProtocols: List<String>

/** Needed to run suspend functions in tests (primarily because JS can't just wait without coroutines), see KT-22228 */
internal expect fun runTest(block: suspend () -> Unit)

/** Runs [block] for all supported protocols on testing platform, providing bind and connect addresses for ZMQ sockets */
internal inline fun forAllProtocols(port: Int, block: (bindAddress: String, connectAddress: String) -> Unit) {
    supportedProtocols.withIndex().forEach { (index, protocol) ->
        val newPort = port + index
        when (protocol) {
            "inproc" -> block("inproc://$newPort", "inproc://$newPort")
            "ipc" -> block("ipc://test/ipc/$newPort", "ipc://test/ipc/$newPort")
            else -> block("$protocol://*:$newPort", "$protocol://localhost:$newPort")
        }
    }
}

/** Delays a bit to wait for some condition to occur. Returns faster if the condition eventually happens. */
internal suspend inline fun waitForCondition(timeout: Int, condition: () -> Boolean) {
    repeat(timeout / 10 + 1) {
        delay(10L)
        if (condition()) return
    }
}