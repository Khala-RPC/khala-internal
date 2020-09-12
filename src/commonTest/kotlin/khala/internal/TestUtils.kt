package khala.internal

internal expect val supportedProtocols: List<String>

internal expect fun checkForEvent(block: () -> Boolean, delay: Int, repeatCount: Int)