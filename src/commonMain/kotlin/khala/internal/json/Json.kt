package khala.internal.json

/*
/** Any? must be null, bool, int, double, string, array<any?> or map<string, any?> */
internal expect fun jsonToString(json: Any?): String
internal expect fun stringToJson(string: String): Any?
*/