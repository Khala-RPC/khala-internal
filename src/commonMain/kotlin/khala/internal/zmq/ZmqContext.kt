package khala.internal.zmq


internal expect class ZmqContext() {

    fun createAndConnectDealer(address: String): ZmqSocket
    fun createAndBindDealer(address: String): ZmqSocket
    fun createAndBindRouter(address: String): ZmqSocket

    fun close()

}