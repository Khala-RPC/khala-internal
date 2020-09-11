package khala.internal.zmq

internal sealed class LoopQuery<S>

internal class InvokeQuery<S>(val block: (LoopState<S>) -> Unit) : LoopQuery<S>()

internal class StopQuery<S> : LoopQuery<S>()
