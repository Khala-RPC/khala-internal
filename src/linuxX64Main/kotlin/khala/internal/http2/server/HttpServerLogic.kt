package khala.internal.http2.server

import khala.internal.cinterop.nghttp2.*
import khala.internal.cinterop.nghttp2.ssize_t
import kotlinx.cinterop.*
import mu.KotlinLogging
import platform.posix.*
import platform.posix.int32_t

private val logger = KotlinLogging.logger {}

internal fun sendResponse(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    nva: CPointer<nghttp2_nv>?,
    nvLength: size_t,
    fd: Int
): Int = memScoped {
    val dataPrd = cValue<nghttp2_data_provider> {
        source.fd = fd
        read_callback = fileReadCallback
    }
    val rv = nghttp2_submit_response(session, streamId, nva, nvLength, dataPrd.ptr)
    if (rv != 0) {
        logger.warn { "Fatal error: ${nghttp2_strerror(rv)}" }
        -1
    } else 0
}

/**
 * Reads the contents of the file to create a response.
 * If an error occurs while reading the file, we return NGHTTP2_ERR_TEMPORAL_CALLBACK_FAILURE.
 * This tells the library to send RST_STREAM to the stream. When all data has been read,
 * the NGHTTP2_DATA_FLAG_EOF flag is set to signal nghttp2 that we have finished reading the file.
 */
internal fun fileReadCallback(
    session: CPointer<nghttp2_session>?,
    streamId: int32_t,
    buf: CPointer<ByteVar>?,
    length: size_t,
    dataFlags: CPointer<UIntVar>?,
    source: CPointer<nghttp2_data_source>?,
    userData: COpaquePointer
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