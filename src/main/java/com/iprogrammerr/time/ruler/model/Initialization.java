package com.iprogrammerr.time.ruler.model;

import java.util.concurrent.Callable;

public class Initialization<T> {

    private final Callable<T> source;
    private T value;

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
