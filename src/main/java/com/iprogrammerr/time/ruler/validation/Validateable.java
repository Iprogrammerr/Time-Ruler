package com.iprogrammerr.time.ruler.validation;

public interface Validateable<T> {

    T value();

    boolean isValid();
}
