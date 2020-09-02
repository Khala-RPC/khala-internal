package kscience.khala.internal

import czmq.zloop_new
import json.json_c_version_num
import msgpack.msgpack_sbuffer_new

fun main() {
    println("hello world")
    zloop_new()
    msgpack_sbuffer_new()
    json_c_version_num()
}