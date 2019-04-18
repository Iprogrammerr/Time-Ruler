package com.iprogrammerr.time.ruler.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TypedMap {

    private final Map<String, List<String>> source;

    public TypedMap(Map<String, List<String>> source) {
        this.source = source;
    }

    public String string(String key, String defaultValue) {
        List<String> values = source.getOrDefault(key, Collections.singletonList(defaultValue));
        String value;
        if (values.isEmpty()) {
            value = defaultValue;
        } else {
            value = values.get(0);
        }
        return value;
    }

    public int integer(String key, int defaultValue) {
        List<String> values = source.getOrDefault(key, Collections.singletonList(String.valueOf(defaultValue)));
        int value;
        try {
            if (values.isEmpty()) {
                value = defaultValue;
            } else {
                value = Integer.parseInt(values.get(0));
            }
        } catch (Exception e) {
            value = defaultValue;
        }
        return value;
    }
}
