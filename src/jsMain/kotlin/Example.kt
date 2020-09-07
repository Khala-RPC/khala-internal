
fun main() {
    console.log("JS!")
    val zmq = js("require(\"@prodatalab/jszmq\")")
    console.log(zmq.socket("dealer"))
}