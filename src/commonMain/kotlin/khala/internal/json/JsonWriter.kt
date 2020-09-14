package khala.internal.json

/** Any? must be null, bool, int, double, string, list<any?> or map<string, any?> */
internal expect fun writeJson(jsonObject: Any?): String