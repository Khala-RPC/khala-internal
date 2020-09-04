
fun main() {
    console.log("NodeJS!")
    console.log(js("require(\"zeromq\")"))
    val zmq = js("require(\"zeromq\")")
    console.log(zmq)
    val push = zmq.Push
    console.log(zmq.Push)
    console.log(js("(new (require(\"zeromq\")).Push).send(\"lol\")"))
}