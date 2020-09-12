package khala.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async

internal actual val supportedProtocols: List<String> = listOf("ws", "tcp", "inproc") // ipc uses UNIX domain sockets

internal external fun setTimeout(block: () -> Unit, delay: Int, vararg args: Any? = definedExternally)

internal actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.async { block() }.asPromise()

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int, finally: () -> Unit) {
    //val setTimeout = js("function(f, d) { return setTimeout(f, d); }")
    var repeats = 0
    fun timeoutFunc() {
        println("LOL")
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                setTimeout( { timeoutFunc() }, delay)
            }
            else finally()
        }
        else finally()
    }
    //setTimeout(::timeoutFunc, delay)
    setTimeout({ timeoutFunc() }, delay)

    /*
    val setInterval = js("function(f, d) { return setInterval(f, d); }")
    val clearInterval = js("function(id) { return clearInterval(id); }")
    var repeats = 0
    var id = 0
    id = setInterval({
        println(repeats)
        if (block() || (repeatCount >= 1 && ++repeats >= repeatCount)) {
            clearInterval(id)
        }
    }, delay) as Int*/
}