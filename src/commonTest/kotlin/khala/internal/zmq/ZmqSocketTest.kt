package khala.internal.zmq

import khala.internal.supportedProtocols

internal fun twoDealers(protocol: String): Pair<ZmqSocket, ZmqSocket> {
    val ctx = ZmqContext()
    val sock1 = ctx.createAndBindDealer("$protocol://*:12345")
    val sock2 = ctx.createAndConnectDealer("$protocol://localhost:12345")
    return Pair(sock1, sock2)
}

internal fun forAllProtocols(block: (protocol: String) -> Unit) {
    supportedProtocols.forEach(block)
}
