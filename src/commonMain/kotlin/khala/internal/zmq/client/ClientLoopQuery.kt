package khala.internal.zmq.client

internal sealed class ClientLoopQuery<L, S>

internal class InvokeQuery<L, S>(val block: ClientLoopScope<L, S>.(L) -> Unit) : ClientLoopQuery<L, S>()

internal class StopQuery<L, S> : ClientLoopQuery<L, S>()
