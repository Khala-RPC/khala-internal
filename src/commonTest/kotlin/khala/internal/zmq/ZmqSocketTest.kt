package khala.internal.zmq

import khala.internal.supportedProtocols
import khala.internal.zmq.bindings.ZmqContext
import khala.internal.zmq.bindings.ZmqSocket

internal fun twoDealers(protocol: String): Pair<ZmqSocket, ZmqSocket> {
    val ctx = ZmqContext()
    val sock1 = ctx.createAndBindDealer("$protocol://*:12345")
    val sock2 = ctx.createAndConnectDealer("$protocol://localhost:12345")
    return Pair(sock1, sock2)
}

internal inline fun forAllProtocols(port: Int, block: (bindAddress: String, connectAddress: String) -> Unit) {
    supportedProtocols.withIndex().forEach {
        val newPort = port + it.index
        if (it.value == "inproc") block("inproc://$newPort", "inproc://$newPort")
        else block("${it.value}://*:$newPort", "${it.value}://localhost:$newPort")
    }
}
