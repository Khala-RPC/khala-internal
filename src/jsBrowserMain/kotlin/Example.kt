import kotlinx.browser.window

fun main() {
    console.log("BrowserJS!")
    window.alert("AlertBrowserJS!")
    val zmq = js("require(\"@prodatalab/jszmq\")")
    console.log(zmq.socket("dealer"))
    JsCommonClass().lol()
}