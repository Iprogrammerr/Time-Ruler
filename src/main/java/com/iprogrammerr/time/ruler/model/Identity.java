package com.iprogrammerr.time.ruler.model;

import javax.servlet.http.HttpServletRequest;

public interface Identity<T> {

    void create(T value, HttpServletRequest request);

    boolean isValid(HttpServletRequest request);

    T value(HttpServletRequest request);
}
