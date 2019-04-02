package com.iprogrammerr.time.ruler.database;

import java.util.ArrayList;
import java.util.List;

public class Record {

    private final String table;
    private final List<String> keys;
    private final List<Object> values;

    public Record(String table) {
        this.table = table;
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public Record put(String key, Object value) {
        keys.add(key);
        values.add(value);
        return this;
    }

    public String table() {
        return table;
    }

    public List<String> keys() {
        return keys;
    }

    public List<Object> values() {
        return values;
    }
}
