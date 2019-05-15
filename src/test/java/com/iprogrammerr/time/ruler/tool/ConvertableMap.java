package com.iprogrammerr.time.ruler.tool;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertableMap {

    private final Map<String, Object> source;

    public ConvertableMap() {
        source = new HashMap<>();
    }

    public ConvertableMap put(String key, Object value) {
        source.put(key, value);
        return this;
    }

    public Map<String, Object> get() {
        return source;
    }

    public Map<String, List<String>> getConverted() {
        Map<String, List<String>> converted = new HashMap<>();
        for (Map.Entry<String, Object> e : source.entrySet()) {
            converted.put(e.getKey(), Collections.singletonList(e.getValue().toString()));
        }
        return converted;
    }
}
