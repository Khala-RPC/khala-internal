package khala.internal.json

/** Any? must be null, bool, int, double, string, list<any?> or map<string, any?> */
internal expect fun parseJson(jsonString: String): Any?