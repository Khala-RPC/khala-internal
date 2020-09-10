package khala.internal.zmq

internal sealed class LoopQuery<S>

internal class InvokeQuery<S>(block: (S) -> Unit) : LoopQuery<S>()

internal class StopQuery<S>() : LoopQuery<S>()
