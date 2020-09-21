package khala.internal.events.streams

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * This file contains different modes of asynchronous streams.
 * Stream mode actually defines how streams deal with very high load situations.
 */
typealias StreamMode = Byte

/**
 * Default streaming mode.
 * Sending stream sends all elements immediately.
 * Only default ZeroMQ message buffering is used, no additional buffering is provided.
 * Under very high load:
 *  - Sending stream from client will block until new elements may be safely sent.
 *  - Sending stream from server will drop messages and then automatically resend them
 *    after short period of time until they are finally delivered in preserved order.
 * This behavior depends on how ZeroMQ High water mark works on Dealer and Router sockets.
 * If you are familiar with ZeroMQ, you may set custom high water mark:
 *  - For client, using KhalaClient.setZmqHighWaterMark function before calling any remote functions.
 *  - For server, passing HWM to its constructor when creating KhalaServer instance.
 */
@ExperimentalJsExport
@JsExport
const val DEFAULT: StreamMode = 0
