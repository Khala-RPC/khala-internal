package khala.internal.zmq.bindings

/**
 * Binary block that is received and sent inside ZMQ messages.
 * This is NOT ByteArray because ByteArray is not really interoperable with some platform-specific code.
 */
expect class ZmqBinaryData

internal expect fun ZmqBinaryData.toByteArray(): ByteArray

internal expect fun ByteArray.toZmqBinaryData(): ZmqBinaryData