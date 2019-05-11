package com.iprogrammerr.time.ruler.model.user;

public interface UsersActualization {

    void activate(long id);

    void updateName(long id, String name);

    void updateEmail(long id, String email);

    void updatePassword(long id, String password);
}
