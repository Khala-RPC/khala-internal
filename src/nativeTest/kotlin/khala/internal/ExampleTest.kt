package khala.internal

import khala.internal.cinterop.czmq.zloop_new
import khala.internal.cinterop.json.json_c_version_num
import khala.internal.cinterop.msgpack.msgpack_sbuffer_new
import kotlin.test.Test

@Test
fun lol() {
    println("TESTY")
    println(zloop_new())
    println(msgpack_sbuffer_new())
    println(json_c_version_num())
}