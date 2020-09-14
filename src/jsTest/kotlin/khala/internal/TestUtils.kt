package khala.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async

//TODO Create a workaround to test at least SOMETHING in browser
internal actual val supportedProtocols: List<String> = listOf() // browser doesn't support binding sockets

internal actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.async { block() }.asPromise()
