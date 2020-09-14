package khala.internal.zmq.client

internal sealed class ClientLoopQuery<L>

internal class InvokeQuery<L>(val block: ClientLoopScope.(L) -> Unit) : ClientLoopQuery<L>()

internal class StopQuery<L> : ClientLoopQuery<L>()
