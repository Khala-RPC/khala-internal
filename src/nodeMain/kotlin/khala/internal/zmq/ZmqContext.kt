package khala.internal.zmq

internal val zmq = js("""require('zeromq')""")

internal class ZmqContext() {

    fun createDealerSocket(): ZmqSocket = ZmqSocket(zmq.socket("dealer"))

    fun createRouterSocket(): ZmqSocket = ZmqSocket(zmq.socket("router"))

    fun close() { /* do nothing */ }

}