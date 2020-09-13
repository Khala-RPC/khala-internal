@file:JvmName("TestUtilsActualKt")
package khala.internal

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal actual val supportedProtocols: List<String> = listOf("tcp", "inproc") // ipc works only between JVM nodes

internal actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }

internal actual fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int, finally: () -> Unit) {
    var repeats = 0
    while (true) {
        if (!block()) {
            if (repeatCount < 1 || ++repeats < repeatCount) {
                Thread.sleep(delay.toLong())
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
