package com.iprogrammerr.time.ruler.model.user;

import java.util.List;
import java.util.Optional;

public interface Users {

    List<User> all();

    List<User> allInactive();

    User user(long id);

    Optional<User> withEmail(String email);

    Optional<User> withName(String name);

    long create(String name, String email, String password);

    void update(User user);
}
