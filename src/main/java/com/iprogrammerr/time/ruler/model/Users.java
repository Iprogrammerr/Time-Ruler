package com.iprogrammerr.time.ruler.model;

import java.util.List;

public interface Users {

    List<User> all();

    long create(User user);
}
