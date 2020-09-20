package khala.internal

import kotlinx.coroutines.runBlocking

internal actual val supportedProtocols: List<String> = listOf("tcp", "inproc") // ipc uses UNIX domain sockets

internal actual fun runTest(block: suspend () -> Unit): Unit = runBlocking { block() }