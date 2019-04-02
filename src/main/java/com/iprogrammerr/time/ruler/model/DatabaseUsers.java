package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.database.DatabaseSession;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUsers implements Users {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String ACTIVE = "active";
    private final DatabaseSession session;

    public DatabaseUsers(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public List<User> all() {
        return session.select(r -> {
            List<User> users = new ArrayList<>();
            if (r.first()) {
                do {
                    users.add(
                        new User(r.getLong(ID), r.getString(NAME), r.getString(EMAIL), r.getString(PASSWORD),
                            r.getBoolean(ACTIVE))
                    );
                } while (r.next());
            }
            return users;
        }, "SELECT * FROM user");
    }

    @Override
    public long create(User user) {
        return 0;
    }
}
