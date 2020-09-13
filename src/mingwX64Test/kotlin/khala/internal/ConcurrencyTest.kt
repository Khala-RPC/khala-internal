package khala.internal

import kotlinx.coroutines.*
import platform.posix.sleep
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.native.internal.test.testLauncherEntryPoint
import kotlin.test.Ignore
import kotlin.test.Test

class ConcurrencyTest {

    @Test
    fun testCoroutinesConcurrency() = runTest {
        val def = CompletableDeferred<Boolean>()
        GlobalScope.launch(Dispatchers.Default) {
            delay(100)
            def.complete(true)
        }
        val res = def.await()
        println(res)
    }

    /* CompletableDeferred on Native cannot be used from different workers. This test goes into deadlock */
    @Ignore
    @Test
    fun testWorkerConcurrency() = runTest {
        val def = CompletableDeferred<Boolean>()
        val worker = Worker.start()
        println("Test started")
        worker.execute(TransferMode.SAFE, { def.freeze() }) {
            println("Worker started")
            @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
            sleep(1)
            println("Waking up")
            it.complete(false)
            println("Worker finished")
        }
        println("Waiting for deferred")
        val res = def.await()
        println("Deferred finished")
        println(res)
    }
}