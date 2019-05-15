package com.iprogrammerr.time.ruler.model;

import java.util.List;
import java.util.Map;

public class TypedMap {

    private static final String TRUE = Boolean.toString(true);
    private final Map<String, List<String>> source;

    public TypedMap(Map<String, List<String>> source) {
        this.source = source;
    }

    public String stringValue(String key, String defaultValue) {
        List<String> values = source.get(key);
        String value;
        if (noValues(values)) {
            value = defaultValue;
        } else {
            value = values.get(0);
        }
        return value;
    }

    private boolean noValues(List<String> values) {
        return values == null || values.isEmpty();
    }

    public String stringValue(String key) {
        return stringValue(key, "");
    }

    public long longValue(String key, long defaultValue) {
        List<String> values = source.get(key);
        long value;
        try {
            if (noValues(values)) {
                value = defaultValue;
            } else {
                value = Long.parseLong(values.get(0));
            }
        } catch (Exception e) {
            value = defaultValue;
        }
        return value;
    }

    public int intValue(String key, int defaultValue) {
        return (int) longValue(key, defaultValue);
    }

    public boolean booleanValue(String key, boolean defaultValue) {
        boolean value;
        List<String> values = source.get(key);
        if (noValues(values)) {
            value = defaultValue;
        } else {
            value = values.get(0).equalsIgnoreCase(TRUE);
        }
        return value;
    }

    public boolean booleanValue(String key) {
        return booleanValue(key, false);
    }
}
