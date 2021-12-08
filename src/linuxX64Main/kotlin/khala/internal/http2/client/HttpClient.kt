package khala.internal.http2.client

import khala.internal.cinterop.libevent.*
import khala.internal.cinterop.nghttp2.*
import khala.internal.cinterop.openssl.SSL_CTX
import khala.internal.http2.server.HttpContext
import khala.internal.http2.server.startServerListening
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.size_t
import platform.posix.uint16_t

private val logger = KotlinLogging.logger {}

/**
 * Launches the HTTP/2 client
 */
// run
fun runClient(
    schema: String,
    authority: String,
    path: String,
    host: String,
    port: uint16_t
) = memScoped {
    //TODO SSL stuff (see https://nghttp2.org/documentation/tutorial-client.html)
    val eventBase = event_base_new()
    val sessionData = createClientHttpSessionData(eventBase)
    sessionData.streamData = HttpClientStreamData(
        schema.cstr.ptr,
        authority.cstr.ptr,
        path.cstr.ptr,
        schema.length.convert(),
        authority.length.convert(),
        path.length.convert(),
        0
    )
    initiateConnection(eventBase, null, host.cstr.ptr, port, sessionData)
    event_base_loop(eventBase, 0)
    event_base_free(eventBase)
}

/**
 * initiate_connection() creates a bufferevent for the connection and sets up three callbacks: readcb, writecb, and eventcb.
 */
// initiate_connection
internal fun initiateConnection(
    eventBase: CPointer<event_base>?,
    sslCtx: CPointer<SSL_CTX>?,
    host: CPointer<ByteVar>?,
    port: uint16_t,
    sessionData: HttpClientSessionData?
) {
    //TODO SSL Stuff (see https://nghttp2.org/documentation/tutorial-client.html)
    val bufferEvent = bufferevent_socket_new(
        eventBase,
        -1,
        (BEV_OPT_DEFER_CALLBACKS or BEV_OPT_CLOSE_ON_FREE).convert()
    )
    bufferevent_enable(bufferEvent, (EV_READ or EV_WRITE).convert())
    bufferevent_setcb(
        bufferEvent,
        staticCFunction(::clientReadCallback),
        staticCFunction(::clientWriteCallback),
        staticCFunction(::clientEventCallback),
        sessionData?.stableRef?.asCPointer()
    )
    val rv = bufferevent_socket_connect_hostname(bufferEvent, sessionData?.dnsBase, AF_UNSPEC, host?.toKString(), port.convert())
    if (rv != 0) {
        logger.warn { "Could not connect to the remote host ${host?.toKString()}:$port" }
    }
    sessionData?.bufferEvent = bufferEvent
}

/**
 * HTTP/2 connection begins by sending the client connection preface,
 * which is a 24 byte magic byte string (NGHTTP2_CLIENT_MAGIC),
 * followed by a SETTINGS frame. The 24 byte magic string is sent automatically by nghttp2.
 * We send the SETTINGS frame in send_client_connection_header().
 */
// send_client_connection_header
internal fun sendClientConnectionHeader(sessionData: HttpClientSessionData?) = memScoped {
    val iv = alloc<nghttp2_settings_entry> {
        settings_id = NGHTTP2_SETTINGS_MAX_CONCURRENT_STREAMS.convert()
        value = 100u //TODO remove max streams cap? also in server
    }
    val rv = nghttp2_submit_settings(sessionData?.session, NGHTTP2_FLAG_NONE.convert(), iv.ptr, 1)
    if (rv != 0) {
        logger.warn { "Could not submit SETTINGS: ${nghttp2_strerror(rv)}" }
    }
}

/**
 * After the transmission of the client connection header, we enqueue the HTTP request in the submit_request() function.
 * o queue the HTTP request, we call nghttp2_submit_request().
 * The stream_data is passed via the stream_user_data parameter, which is helpfully later passed back to callback functions.
 */
// submit_request
internal fun clientSubmitRequest(sessionData: HttpClientSessionData?) = memScoped {
    val streamData = sessionData?.streamData
    val headers = allocArray<nghttp2_nv>(4)
    makeNv(headers[0], ":method", "GET")
    makeNv(headers[1], ":scheme", streamData?.schema, streamData?.schemaLength ?: 0u)
    makeNv(headers[2], ":authority", streamData?.authority, streamData?.authorityLength ?: 0u)
    makeNv(headers[3], ":path", streamData?.path, streamData?.pathLength ?: 0u)
    logger.info { "Request headers: $headers" }
    val streamId = nghttp2_submit_request(
        sessionData?.session,
        null,
        headers,
        4,
        null,
        streamData?.stableRef?.asCPointer()
    )
    if (streamId < 0) {
        logger.warn { "Could not submit HTTP request: ${nghttp2_strerror(streamId)}" }
    }
    streamData?.streamId = streamId
}

private fun MemScope.makeNv(header: nghttp2_nv, name: String, value: CPointer<ByteVar>?, valueLength: size_t) {
    header.name = name.cstr.ptr.reinterpret()
    header.namelen = name.length.convert()
    header.value = value?.reinterpret()
    header.valuelen = valueLength
    header.flags = NGHTTP2_FLAG_NONE.convert()
}

private fun MemScope.makeNv(header: nghttp2_nv, name: String, value: String) {
    header.name = name.cstr.ptr.reinterpret()
    header.namelen = name.length.convert()
    header.value = value.cstr.ptr.reinterpret()
    header.valuelen = value.length.convert()
    header.flags = NGHTTP2_FLAG_NONE.convert()
}