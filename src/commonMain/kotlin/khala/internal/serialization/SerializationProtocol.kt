package khala.internal.serialization

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

typealias SerializationProtocol = Byte

@ExperimentalJsExport
@JsExport
const val BINARY: SerializationProtocol = 0

@ExperimentalJsExport
@JsExport
const val JSON: SerializationProtocol = 1