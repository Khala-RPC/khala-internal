package khala.internal

internal expect val supportedProtocols: List<String>

internal expect fun runTest(block: suspend () -> Unit)

internal expect fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int, finally: () -> Unit)

internal fun checkForEvent(delay: Int, repeatCount: Int, block: () -> Boolean, finally: () -> Unit) {
    checkForEvent(block, delay, repeatCount, finally)
}