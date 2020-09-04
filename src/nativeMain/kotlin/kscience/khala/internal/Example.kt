package kscience.khala.internal

import kscience.khala.internal.cinterop.czmq.zloop_new
import kscience.khala.internal.cinterop.json.json_c_version_num
import kscience.khala.internal.cinterop.msgpack.msgpack_sbuffer_new

fun main() {
    println("hello world")
    println(zloop_new())
    println(msgpack_sbuffer_new())
    println(json_c_version_num())
}