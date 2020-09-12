package khala.internal

import kotlinx.cinterop.convert
import kotlinx.coroutines.runBlocking
import platform.posix.sleep

internal actual val supportedProtocols: List<String> = listOf("tcp", "inproc") // ipc uses UNIX domain sockets

internal actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int, finally: () -> Unit) {
    var repeats = 0
    while (true) {
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                sleep(delay.convert())
            }
            else {
                finally()
                break
            }
        }
        else {
            finally()
            break
        }
    }
}