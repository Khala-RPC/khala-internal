package khala.internal.zmq

internal class ZmqMsg {

    val msg: ArrayDeque<ByteArray> = ArrayDeque()

    constructor(message: dynamic) {

    }

    override fun toString(): String = msg.toString()

}