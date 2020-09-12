package khala.internal

internal actual val supportedProtocols: List<String> = listOf("ws", "tcp", "inproc") // ipc uses UNIX domain sockets

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int) {
    val setTimeout = js("function(f, d) { setTimeout(f, d); }")
    var repeats = 0
    fun timeoutFunc() {
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                setTimeout(::timeoutFunc, delay)
            }
        }
    }
    setTimeout(::timeoutFunc, delay)
}