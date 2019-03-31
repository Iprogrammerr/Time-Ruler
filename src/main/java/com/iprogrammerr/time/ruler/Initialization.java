package com.iprogrammerr.time.ruler;

import java.util.concurrent.Callable;

public class Initialization<T> {

    private T value;
    private final Callable<T> source;

    public Initialization(Callable<T> source) {
        this.source = source;
    }

    public T value() {
        if (value == null) {
            try {
                value = source.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }
}
