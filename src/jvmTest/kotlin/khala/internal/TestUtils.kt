package khala.internal

internal actual val supportedProtocols: List<String> = listOf("tcp", "inproc") // ipc works only between JVM nodes

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int) {
    var repeats = 0
    while (true) {
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                Thread.sleep(delay.toLong())
            }
            else break
        }
        else break
    }
}