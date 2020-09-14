package khala.internal

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async

internal actual val supportedProtocols: List<String> = listOf("ws", "tcp", "inproc") // ipc uses UNIX domain sockets

internal actual fun runTest(block: suspend () -> Unit): dynamic = GlobalScope.async { block() }.asPromise()
