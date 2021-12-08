package khala.internal.http2.server

import kotlinx.cinterop.StableRef
import platform.posix.close
import platform.posix.int32_t

/**
 * A single HTTP/2 session can have multiple streams.
 * To manage them, we use a doubly linked list:
 * The first element of this list is pointed to by the root->next in http2_session_data.
 * Initially, root->next is NULL.
 */
// struct http2_stream_data
class HttpServerStreamData(
    var prev: HttpServerStreamData?,
    var next: HttpServerStreamData?,
    var requestPath: String?,
    val streamId: int32_t,
    var fd: Int
) {
    val stableRef = StableRef.create(this)
}

// create_http2_stream_data
internal fun createStreamData(
    sessionData: HttpServerSessionData?,
    streamId: int32_t
): HttpServerStreamData {
    val streamData = HttpServerStreamData(
        prev = null,
        next = null,
        requestPath = null,
        streamId = streamId,
        fd = -1
    )
    addStream(sessionData, streamData)
    return streamData
}

// delete_http2_stream_data
internal fun deleteStreamData(streamData: HttpServerStreamData) {
    if (streamData.fd != -1) {
        close(streamData.fd)
    }
    streamData.stableRef.dispose()
}

// add_stream
internal fun addStream(
    sessionData: HttpServerSessionData?,
    streamData: HttpServerStreamData
) {
    streamData.next = sessionData?.root?.next
    sessionData?.root?.next = streamData
    streamData.prev = sessionData?.root
    streamData.next?.let {
        it.prev = streamData
    }
}

// remove_stream
internal fun removeStream(
    sessionData: HttpServerSessionData?,
    streamData: HttpServerStreamData?
) {
    streamData?.prev?.next = streamData?.next
    streamData?.next?.let {
        it.prev = streamData.prev
    }
}