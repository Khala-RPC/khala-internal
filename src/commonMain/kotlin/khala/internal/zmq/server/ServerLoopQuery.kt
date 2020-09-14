package khala.internal.zmq.server

internal sealed class ServerLoopQuery<L>

internal class InvokeQuery<L>(val block: ServerLoopScope.(L) -> Unit) : ServerLoopQuery<L>()

internal class StopQuery<L> : ServerLoopQuery<L>()
