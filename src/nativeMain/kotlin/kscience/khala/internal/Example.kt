package kscience.khala.internal

import czmq.zloop_new
import json.json_c_version_num
import msgpack.msgpack_sbuffer_new

fun main() {
    println("hello world")
    println(zloop_new())
    println(msgpack_sbuffer_new())
    println(json_c_version_num())
}