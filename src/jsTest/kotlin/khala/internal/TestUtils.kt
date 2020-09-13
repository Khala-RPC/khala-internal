package khala.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async

internal actual val supportedProtocols: List<String> = listOf() // browser doesn't support binding sockets

internal actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.async { block() }.asPromise()

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int, finally: () -> Unit) {
    val setTimeout = js("function(f, d) { setTimeout(f, d); }")
    var repeats = 0
    fun timeoutFunc() {
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                setTimeout(::timeoutFunc, delay)
            }
            else finally()
        }
        else finally()
    }
    setTimeout(::timeoutFunc, delay)
}