package com.iprogrammerr.time.ruler.model.user;

import java.util.List;

public interface Users {

    List<User> all();

    List<User> allInactive();

    User user(long id);

    boolean existsWithEmailOrName(String emailOrName);

    User byEmailOrName(String emailOrName);

    long create(String name, String email, String password);

    void update(User user);
}
