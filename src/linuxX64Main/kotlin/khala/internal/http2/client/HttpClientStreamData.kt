package khala.internal.http2.client

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.StableRef
import platform.posix.free
import platform.posix.int32_t
import platform.posix.size_t

// http2_stream_data
//TODO We currently support only one stream. Make linked list.
class HttpClientStreamData(
    //val uri: CPointer<ByteVar>?,
    //TODO val u: CPointer<http_parser_url>?,
    val schema: CPointer<ByteVar>?,
    val authority: CPointer<ByteVar>?,
    val path: CPointer<ByteVar>?,
    val schemaLength: size_t,
    val authorityLength: size_t,
    val pathLength: size_t,
    var streamId: int32_t
) {
    val stableRef = StableRef.create(this)
}

internal fun deleteClientHttpStreamData(streamData: HttpClientStreamData) {
    free(streamData.schema)
    free(streamData.authority)
    free(streamData.path)
    streamData.stableRef.dispose()
}