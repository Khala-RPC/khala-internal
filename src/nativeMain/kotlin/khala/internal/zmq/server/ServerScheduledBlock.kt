package khala.internal.zmq.server

internal class ServerScheduledBlock<L>(
    val scheduleTimeMillis: Long,
    val block: ServerLoopScope<L>.(L) -> Unit
) : Comparable<ServerScheduledBlock<L>> {

    override fun compareTo(other: ServerScheduledBlock<L>): Int {
        return scheduleTimeMillis.compareTo(other.scheduleTimeMillis)
    }

}