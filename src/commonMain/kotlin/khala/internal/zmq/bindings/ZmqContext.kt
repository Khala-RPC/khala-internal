package khala.internal.zmq.bindings

internal expect object ZmqContext {

    fun createAndConnectDealer(address: String): ZmqSocket
    fun createAndBindDealer(address: String): ZmqSocket
    fun createAndBindRouter(address: String): ZmqSocket

}