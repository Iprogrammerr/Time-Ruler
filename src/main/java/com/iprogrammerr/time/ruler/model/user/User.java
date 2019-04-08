package com.iprogrammerr.time.ruler.model.user;

import java.sql.ResultSet;
import java.util.Objects;

public class User {

    public static final String TABLE = "user";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ACTIVE = "active";

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

    public User(ResultSet resultSet) throws Exception {
        this(resultSet.getLong(ID), resultSet.getString(NAME), resultSet.getString(EMAIL),
            resultSet.getString(PASSWORD), resultSet.getBoolean(ACTIVE));
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

    @Override
    public boolean equals(Object other) {
        if (other != null && User.class.isAssignableFrom(other.getClass())) {
            User user = (User) other;
            return id == user.id && name.equals(user.name) && email.equals(user.email)
                && password.equals(user.password) && active == user.active;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, active);
    }
}
