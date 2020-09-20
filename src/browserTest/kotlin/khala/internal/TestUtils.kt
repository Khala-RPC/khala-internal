package khala.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async

internal actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.async { block() }.asPromise()

internal actual val supportedProtocols: List<String> = listOf() // browser doesn't support binding sockets
