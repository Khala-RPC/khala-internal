package khala.internal.http2

import khala.internal.cinterop.nghttp2.nghttp2_session_callbacks_new
import khala.internal.cinterop.nghttp2.nghttp2_session_client_new
import khala.internal.cinterop.nghttp2.nghttp2_session
import khala.internal.cinterop.nghttp2.nghttp2_session_callbacks
import khala.internal.cinterop.openssl.SSL_new
import kotlinx.cinterop.*
import kotlin.test.Test

class HttpSessionCallbacksTest {

    @Test
    fun `test callbacks init`() = memScoped<Unit> {
        val callbacksPtr: CPointer<CPointerVar<nghttp2_session_callbacks>> = alloc<CPointerVar<nghttp2_session_callbacks>>().ptr
        val sessionPtr: CPointer<CPointerVar<nghttp2_session>> = alloc<CPointerVar<nghttp2_session>>().ptr
        nghttp2_session_callbacks_new(callbacksPtr)
        val callbacksPointed: CPointerVar<nghttp2_session_callbacks> = callbacksPtr.pointed
        val callbacksValue: CPointer<nghttp2_session_callbacks>? = callbacksPointed.value
        nghttp2_session_client_new(sessionPtr, callbacksValue, null)
    }
}