package khala.internal.http2.server

import platform.posix.close
import platform.posix.free
import platform.posix.int32_t

/**
 * A single HTTP/2 session can have multiple streams.
 * To manage them, we use a doubly linked list:
 * The first element of this list is pointed to by the root->next in http2_session_data.
 * Initially, root->next is NULL.
 */
class HttpStreamData(
    var prev: HttpStreamData?,
    var next: HttpStreamData?,
    var requestPath: String?,
    val streamId: int32_t,
    val fd: Int
)

internal fun createStreamData(
    sessionData: HttpSessionData,
    streamId: int32_t
): HttpStreamData {
    val streamData = HttpStreamData(
        prev = null,
        next = null,
        requestPath = null,
        streamId = streamId,
        fd = -1
    )
    addStream(sessionData, streamData)
    return streamData
}

internal fun deleteStreamData(streamData: HttpStreamData) {
    if (streamData.fd != -1) {
        close(streamData.fd)
    }
}

internal fun addStream(
    sessionData: HttpSessionData,
    streamData: HttpStreamData
) {
    streamData.next = sessionData.root?.next
    sessionData.root?.next = streamData
    streamData.prev = sessionData.root
    streamData.next?.let {
        it.prev = streamData
    }
}

internal fun removeStream(
    sessionData: HttpSessionData,
    streamData: HttpStreamData
) {
    streamData.prev?.next = streamData.next
    streamData.next?.let {
        it.prev = streamData.prev
    }
}