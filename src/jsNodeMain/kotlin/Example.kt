
fun main() {
    console.log("NodeJS!")
    val zmq = js("require(\"zeromq\")")
    console.log(zmq.socket("dealer"))
}