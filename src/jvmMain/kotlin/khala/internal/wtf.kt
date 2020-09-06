package khala.internal

import java.io.File

fun process(f: File) {
    f.listFiles()
        ?.groupBy(File::isDirectory)
        ?.also {
            it[true]?.forEach(::process)
        }
        ?.also {
            it[false]
                ?.let {
                    if (it.any { it.name.contains("msgpack") } && it.none { it.name.contains("libczmq") }) {
                        println(f.absolutePath)
                    }
                }
        }
}

fun processo(f: File) {
    f.listFiles()
        ?.groupBy(File::isDirectory)
        ?.also {
            it[true]?.forEach(::process)
        }
        ?.also {
            it[false]
                ?.let {
                    if (it.any { it.name.contains("cinterop") }) {
                        println(f.absolutePath)
                    }
                }
        }
}

fun main() {
    processo(File("D:/Coding/"))
    processo(File("D:/JetBrains/"))
    //process(File("D:/Soft/"))
    //process(File("D:/Program Files (x86)/"))
    processo(File("C:/"))
}