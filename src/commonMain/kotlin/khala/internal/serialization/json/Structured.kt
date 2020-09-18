package khala.internal.serialization.json

/**
 * JSON parser returns an object of Structured type, which should have one of the following StructuredXXXX types.
 */
typealias Structured = Any?

typealias StructuredNull = Nothing?
typealias StructuredBoolean = Boolean
typealias StructuredInt = Int
typealias StructuredDouble = Double
typealias StructuredString = String
typealias StructuredArray = List<Structured>
typealias StructuredObject = Map<String, Structured>
