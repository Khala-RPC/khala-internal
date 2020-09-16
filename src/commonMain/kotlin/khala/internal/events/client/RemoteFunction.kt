package khala.internal.events.client

/**
 * Link to named remote function which is publicly available to all queries.
 */
data class RemoteFunction(val address: String, val name: String)