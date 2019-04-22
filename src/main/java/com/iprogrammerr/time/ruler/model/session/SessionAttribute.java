package com.iprogrammerr.time.ruler.model.session;

import javax.servlet.http.HttpSession;

public interface SessionAttribute<T> {

    T from(HttpSession session);

    void to(HttpSession session, T value);
}
