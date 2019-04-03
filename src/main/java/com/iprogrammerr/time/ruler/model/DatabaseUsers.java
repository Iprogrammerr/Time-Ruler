package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.Record;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUsers implements Users {

    private final DatabaseSession session;

    public DatabaseUsers(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public List<User> all() {
        return session.select(r -> {
            List<User> users = new ArrayList<>();
            while (r.next()) {
                users.add(new User(r));
            }
            return users;
        }, "SELECT * FROM user");
    }


    @Override
    public long create(String name, String email, String password) {
        return session.create(
            new Record(User.TABLE).put(User.NAME, name).put(User.EMAIL, email).put(User.PASSWORD, password)
                .put(User.ACTIVE, false)
        );
    }

    @Override
    public void update(User user) {
        session.update(
            new Record(User.TABLE)
                .put(User.NAME, user.name).put(User.EMAIL, user.email).put(User.PASSWORD, user.password)
                .put(User.ACTIVE, user.active),
            "id = ?", user.id
        );
    }

    @Override
    public User user(long id) {
        return session.select(r -> {
            if (r.next()) {
                return new User(r);
            }
            throw new RuntimeException(String.format("There is no user of with %d id", id));
        }, "SELECT * FROM user WHERE id = ?", id);
    }
}
