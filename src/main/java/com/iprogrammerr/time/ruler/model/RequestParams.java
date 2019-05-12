package com.iprogrammerr.time.ruler.model;

import io.javalin.Context;

import java.util.List;
import java.util.Map;

public class RequestParams {

    private static final String TRUE = "true";
    private final Map<String, List<String>> params;

    public RequestParams(Map<String, List<String>> params) {
        this.params = params;
    }

    public RequestParams(Context context) {
        this(context.queryParamMap());
    }

    public boolean booleanParam(QueryParamKey key, boolean defaultValue) {
        boolean value;
        List<String> values = params.get(key.value);
        if (noValues(values)) {
            value = defaultValue;
        } else {
            value = values.get(0).equalsIgnoreCase(TRUE);
        }
        return value;
    }

    private boolean noValues(List<String> values) {
        return values == null || values.isEmpty();
    }

    public boolean booleanParam(QueryParamKey key) {
        return booleanParam(key, false);
    }

    public long longParam(QueryParamKey key, long defaultValue) {
        long value;
        List<String> values = params.get(key.value);
        if (noValues(values)) {
            value = defaultValue;
        } else {
            try {
                value = Long.parseLong(values.get(0));
            } catch (Exception e) {
                value = defaultValue;
            }
        }
        return value;
    }

    public int intParam(QueryParamKey key, int defaultValue) {
        return (int) longParam(key, defaultValue);
    }

    public String stringParam(QueryParamKey key, String defaultValue) {
        String value;
        List<String> values = params.get(key.value);
        if (noValues(values)) {
            value = defaultValue;
        } else {
            value = values.get(0);
        }
        return value;
    }

    public String stringParam(QueryParamKey key) {
        return stringParam(key, "");
    }
}
