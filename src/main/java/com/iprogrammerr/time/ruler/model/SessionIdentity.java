package com.iprogrammerr.time.ruler.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionIdentity implements Identity<Long> {

    private static final String IDENTITY = "IDENTITY";

    @Override
    public void create(Long value, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(IDENTITY, value);
    }

    @Override
    public Long value(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new RuntimeException("There is no valid session");
        }
        Object id = session.getAttribute(IDENTITY);
        if (id == null || !Long.class.isAssignableFrom(id.getClass())) {
            throw new RuntimeException(String.format("%s is not proper session id", id));
        }
        return (Long) id;
    }
}
