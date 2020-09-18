package khala.internal.zmq.client

internal class ClientScheduledBlock<L, S>(
    val scheduleTimeMillis: Long,
    val block: ClientLoopScope<L, S>.(L) -> Unit
) : Comparable<ClientScheduledBlock<L, S>> {

    override fun compareTo(other: ClientScheduledBlock<L, S>): Int {
        return scheduleTimeMillis.compareTo(other.scheduleTimeMillis)
    }

}