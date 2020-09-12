package khala.internal.zmq

import khala.internal.supportedProtocols

internal fun twoDealers(protocol: String): Pair<ZmqSocket, ZmqSocket> {
    val ctx = ZmqContext()
    val sock1 = ctx.createAndBindDealer("$protocol://*:12345")
    val sock2 = ctx.createAndConnectDealer("$protocol://localhost:12345")
    return Pair(sock1, sock2)
}

internal inline fun forAllProtocols(port: Int, block: (bindAddress: String, connectAddress: String) -> Unit) {
    supportedProtocols.forEach {
        if (it == "inproc") block("inproc://$port", "inproc://$port")
        else block("$it://*:$port", "$it://localhost:$port")
    }
}
