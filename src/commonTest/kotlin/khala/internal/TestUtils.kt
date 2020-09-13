package khala.internal

import kotlinx.coroutines.delay

internal expect val supportedProtocols: List<String>

internal expect fun runTest(block: suspend () -> Unit)

internal expect fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int, finally: () -> Unit)

internal fun checkForEvent(delay: Int, repeatCount: Int, block: () -> Boolean, finally: () -> Unit) {
    checkForEvent(block, delay, repeatCount, finally)
}

internal suspend inline fun waitForCondition(timeout: Int, condition: () -> Boolean) {
    repeat(timeout / 10 + 1) {
        delay(10L)
        if (condition()) return
    }
}