package khala.internal.zmq.bindings

/*
For everyone whining that this is a singleton that never closes:
Do you close Dispatchers.Default after you are done with it?
*/
internal expect object ZmqContext {

    fun createAndConnectDealer(address: String): ZmqSocket
    fun createAndBindDealer(address: String): ZmqSocket
    fun createAndBindRouter(address: String): ZmqSocket

}