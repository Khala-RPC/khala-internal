@file:JvmName("TestUtilsActualKt")
package khala.internal

import kotlinx.coroutines.runBlocking

internal actual val supportedProtocols: List<String> = listOf("tcp", "inproc") // ipc works only between JVM nodes

internal actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }
