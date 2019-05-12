package com.iprogrammerr.time.ruler.model;

import java.util.HashMap;
import java.util.Map;

public class UrlQueryBuilder {

    private static final String START = "?";
    private static final String SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private final Map<String, String> params;

    public UrlQueryBuilder() {
        this.params = new HashMap<>();
    }

    public UrlQueryBuilder put(String key, Object value) {
        params.put(key, value.toString());
        return this;
    }

    public UrlQueryBuilder put(QueryParamKey key, Object value) {
        return put(key.value, value);
    }

    public String build(String base) {
        StringBuilder builder = new StringBuilder(base);
        builder.append(START);
        boolean first = true;
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!first) {
                builder.append(SEPARATOR);
            } else {
                first = false;
            }
            builder.append(e.getKey()).append(KEY_VALUE_SEPARATOR).append(e.getValue());
        }
        return builder.toString();
    }

    public String build() {
        return build("");
    }
}
