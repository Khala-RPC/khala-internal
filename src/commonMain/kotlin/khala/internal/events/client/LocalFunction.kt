package khala.internal.events.client

import khala.internal.serialization.binary.BinaryPayloadReader
import khala.internal.serialization.binary.BinaryPayloadWriter

/**
 * Local anonymous function which will be available inside a single query and then released.
 * One [LocalFunction] instance may be reused through many queries, [onRelease] will be called after each query finishes.
 */
class LocalFunction(
    /**
     * This function is called from event loop thread.
     * Do NOT make computations inside this thread.
     * Instead each binding MUST compute the function in some sort of thread pool, coroutine dispatcher, etc.
     * Result/error returning callbacks may be called from any thread, these callbacks are fully thread-safe.
     */
    val onCall: (arg: BinaryPayloadReader, returnResult: (BinaryPayloadWriter) -> Unit, returnError: (String) -> Unit) -> Unit,
    /**
     * This callback is invoked when the query using this function has finished and function is no longer available.
     * Native bindings may use this callback to call destructors and free the memory that was allocated for the function.
     * This callback is called from event loop thread, do NOT make computations inside this thread.
     */
    val onRelease: () -> Unit
)