package khala.internal

import kotlinx.cinterop.convert
import platform.posix.sleep

internal actual val supportedProtocols: List<String> = listOf("tcp", "inproc") // ipc uses UNIX domain sockets

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int) {
    var repeats = 0
    while (true) {
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                sleep(delay.convert())
            }
            else break
        }
        else break
    }
}