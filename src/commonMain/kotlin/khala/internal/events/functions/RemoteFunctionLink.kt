package khala.internal.events.functions

import khala.internal.serialization.PayloadReader
import khala.internal.serialization.PayloadWriter

/**
 * Link to a remote function, either anonymous or named.
 */
interface RemoteFunctionLink {

    /**
     * Invokes the remote function.
     * This method is thread-safe and can be called from any thread.
     * Note that [PayloadWriter] is NOT thread-safe,
     * so this method MUST be called from the same thread [arg] belongs to.
     * [onResult] and [onError] callbacks will be called from event loop thread.
     * Do NOT make computations inside event loop thread.
     * Note that [PayloadReader] is NOT thread-safe,
     * so [onResult] MUST safely transfer it to the thread where it will be read.
     * @return Thread-safe callback that may be used to cancel the query.
     * Cancelled query will trigger [onError] callback with "%CANCELLED%" string as an argument.
     */
    fun invoke(
        invoker: (PayloadWriter) -> ((PayloadReader) -> Unit, (String) -> Unit) -> () -> Unit
    )

}