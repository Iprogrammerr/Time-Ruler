package com.iprogrammerr.time.ruler.model;

import java.util.List;

public interface Users {

    List<User> all();

    List<User> allInactive();

    User user(long id);

    boolean existsWithName(String name);

    boolean existsWithEmail(String email);

    User byName(String name);

    User byEmail(String email);

    long create(String name, String email, String password);

    void update(User user);
}
