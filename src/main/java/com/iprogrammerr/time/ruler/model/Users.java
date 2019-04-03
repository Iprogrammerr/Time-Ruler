package com.iprogrammerr.time.ruler.model;

import java.util.List;

public interface Users {

    List<User> all();

    User user(long id);

    long create(String name, String email, String password);

    void update(User user);
}
