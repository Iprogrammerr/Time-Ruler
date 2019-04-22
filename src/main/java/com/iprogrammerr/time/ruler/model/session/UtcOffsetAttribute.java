package com.iprogrammerr.time.ruler.model.session;

import javax.servlet.http.HttpSession;

public class UtcOffsetAttribute implements SessionAttribute<Integer> {

    private final String key;

    public UtcOffsetAttribute(String key) {
        this.key = key;
    }

    public UtcOffsetAttribute() {
        this("utcOffset");
    }

    @Override
    public Integer from(HttpSession session) {
        Object value = session.getAttribute(key);
        if (value == null) {
            throw new RuntimeException(String.format("There is no attribute associated with %s key", key));
        } else if (!Integer.class.isAssignableFrom(value.getClass())) {
            throw new RuntimeException(String.format("%s is not a Integer", value.getClass()));
        }
        return (Integer) value;
    }

    @Override
    public void to(HttpSession session, Integer value) {
        session.setAttribute(key, value);
    }
}
