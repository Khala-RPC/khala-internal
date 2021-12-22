package khala.internal.transport.http2.client

import khala.internal.transport.cinterop.libevent.*
import kotlinx.cinterop.CPointer
import khala.internal.transport.cinterop.nghttp2.nghttp2_session
import khala.internal.transport.cinterop.nghttp2.nghttp2_session_del
import kotlinx.cinterop.StableRef

// struct http2_session_data
class HttpClientSessionData(
    var session: CPointer<nghttp2_session>?,
    var dnsBase: CPointer<evdns_base>?,
    var bufferEvent: CPointer<bufferevent>?,
    var streamData: HttpClientStreamData?
) {
    val stableRef = StableRef.create(this)
}

// create_http2_session_data
internal fun createClientHttpSessionData(eventBase: CPointer<event_base>?): HttpClientSessionData {
    return HttpClientSessionData(
        session = null,
        dnsBase = evdns_base_new(eventBase, 1),
        bufferEvent = null,
        streamData = null
    )
}

// delete_http2_session_data
internal fun deleteClientHttpSessionData(sessionData: HttpClientSessionData?) {
    sessionData ?: return
    //TODO SSL stuff (see https://nghttp2.org/documentation/tutorial-client.html)
    bufferevent_free(sessionData.bufferEvent)
    sessionData.bufferEvent = null
    evdns_base_free(sessionData.dnsBase, 1)
    sessionData.dnsBase = null
    nghttp2_session_del(sessionData.session)
    sessionData.session = null
    sessionData.streamData?.let {
        deleteClientHttpStreamData(it)
    }
    sessionData.streamData = null
}