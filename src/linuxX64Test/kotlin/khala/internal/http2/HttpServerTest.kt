package khala.internal.http2

import khala.internal.http2.server.runServer
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
        logger.error { "error" }
        runServer("12345", "", "")
    }
}