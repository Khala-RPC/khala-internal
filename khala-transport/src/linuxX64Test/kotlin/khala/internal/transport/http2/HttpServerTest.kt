package khala.internal.transport.http2

import khala.internal.transport.http2.server.runServer
import mu.KotlinLogging
import kotlin.test.Test

class HttpServerTest {

    val logger = KotlinLogging.logger {}

    @Test
    fun `test server start`() {
        println("LSADSAFSADGFSKDJGFOKSDHJGJKISHDGJIKHSDJIKFHSJDIF")
        logger.info { "info" }
        logger.debug { "debug" }
        logger.warn { "warn" }
        //runServer("12345", "", "")
    }
}