package com.iprogrammerr.time.ruler.model;

public class User {

    public final long id;
    public final String name;
    public final String email;
    public final String password;
    public final boolean active;

    public User(long id, String name, String email, String password, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", active=" + active +
            '}';
    }
}
