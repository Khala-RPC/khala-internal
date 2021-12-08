package khala.internal.http2.server

import khala.internal.cinterop.nghttp2.*
import khala.internal.cinterop.nghttp2.ssize_t
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.*
import platform.posix.int32_t

private val logger = KotlinLogging.logger {}

// send_response
internal fun sendResponse(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    nva: CPointer<nghttp2_nv>?,
    nvLength: size_t,
    fd: Int
): Int = memScoped {
    val dataPrd = cValue<nghttp2_data_provider> {
        source.fd = fd
        read_callback = staticCFunction(::fileReadCallback)
    }
    val rv = nghttp2_submit_response(session, streamId, nva, nvLength, dataPrd.ptr)
    if (rv != 0) {
        logger.warn { "Fatal error: ${nghttp2_strerror(rv)}" }
        -1
    } else 0
}

// send_response
internal fun sendResponse(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    nva: CPointer<nghttp2_nv>?,
    nvLength: size_t,
    dataSource: COpaquePointer?
): Int = memScoped {
    val dataPrd = cValue<nghttp2_data_provider> {
        source.ptr = dataSource
        read_callback = staticCFunction(::fileReadCallback)
    }
    val rv = nghttp2_submit_response(session, streamId, nva, nvLength, dataPrd.ptr)
    if (rv != 0) {
        logger.warn { "Fatal error: ${nghttp2_strerror(rv)}" }
        -1
    } else 0
}

// error_reply
internal fun errorReply(
    session: CPointer<nghttp2_session>?,
    streamData: HttpServerStreamData?
): Int {
    val pipeFd = IntArray(2)
    val headerName = ":status".cstr
    val headerValue = "404".cstr
    var rv = pipeFd.usePinned {
        pipe(it.addressOf(0))
    }
    if (rv != 0) {
        logger.warn { "Could not create pipe" }
        rv = nghttp2_submit_rst_stream(session, NGHTTP2_FLAG_NONE.convert(), streamData?.streamId ?: -1, NGHTTP2_INTERNAL_ERROR)
        if (rv != 0) {
            logger.warn { "Fatal error: ${nghttp2_strerror(rv)}" }
            return -1
        }
        return 0
    }
    val errorHtml = "<html><head><title>404</title></head><body><h1>404 Not Found</h1></body></html>".cstr
    val writeLength = write(pipeFd[1], errorHtml, (errorHtml.size - 1).convert())
    close(pipeFd[1])
    if (writeLength != (errorHtml.size - 1).convert<ssize_t>()) {
        close(pipeFd[0])
        return -1
    }
    streamData?.fd = pipeFd[0]
    rv = memScoped {
        val header = cValue<nghttp2_nv> {
            name = headerName.ptr.reinterpret()
            namelen = headerName.size.convert()
            value = headerValue.ptr.reinterpret()
            valuelen = headerValue.size.convert()
            flags = NGHTTP2_NV_FLAG_NONE.convert()
        }
        sendResponse(session, streamData?.streamId ?: -1, header.ptr, 1u, pipeFd[0])
    }
    if (rv != 0) {
        close(pipeFd[0])
        return -1
    }
    return 0
}

/**
 * Reads the contents of the file to create a response.
 * If an error occurs while reading the file, we return NGHTTP2_ERR_TEMPORAL_CALLBACK_FAILURE.
 * This tells the library to send RST_STREAM to the stream. When all data has been read,
 * the NGHTTP2_DATA_FLAG_EOF flag is set to signal nghttp2 that we have finished reading the file.
 */
// file_read_callback
internal fun fileReadCallback(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    buf: CPointer<UByteVar>?,
    length: size_t,
    dataFlags: CPointer<UIntVar>?,
    source: CPointer<nghttp2_data_source>?,
    userData: COpaquePointer?
): ssize_t {
    val fd = source?.pointed?.fd ?: return 0
    var r: ssize_t
    while (true) {
        r = read(fd, buf, length)
        if (!(r == -1L && errno == EINTR)) break
    }
    if (r == -1L) return NGHTTP2_ERR_TEMPORAL_CALLBACK_FAILURE.convert()
    if (r == 0L && dataFlags != null) {
        dataFlags[0] = dataFlags[0] or NGHTTP2_DATA_FLAG_EOF
    }
    return r
}

// on_request_recv
internal fun onRequestRecv(
    session: CPointer<nghttp2_session>?,
    sessionData: HttpServerSessionData?,
    streamData: HttpServerStreamData?
): Int {
    logger.info { "onRequestRecv" }
    val headerName = ":status".cstr
    val headerValue = "200".cstr
    val requestPath = streamData?.requestPath ?: run {
        if (errorReply(session, streamData) != 0) {
            return NGHTTP2_ERR_CALLBACK_FAILURE
        }
        return 0
    }
    logger.info { "${sessionData?.clientAddress} GET $requestPath" }
    val rv = memScoped {
        val header = cValue<nghttp2_nv> {
            name = headerName.ptr.reinterpret()
            namelen = headerName.size.convert()
            value = headerValue.ptr.reinterpret()
            valuelen = headerValue.size.convert()
            flags = NGHTTP2_NV_FLAG_NONE.convert()
        }
        val response = "RESPONSE".cstr
        sendResponse(session, streamData?.streamId ?: -1, header.ptr, 1u, response.ptr)
    }
    if (rv != 0) {
        return NGHTTP2_ERR_CALLBACK_FAILURE
    }
    return 0
}