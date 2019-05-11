package com.iprogrammerr.time.ruler.model.user;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.Record;

public class DatabaseUsersActualization implements UsersActualization {

    private final DatabaseSession session;

    public DatabaseUsersActualization(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public void activate(long id) {
        session.update(new Record(User.TABLE).put(User.ACTIVE, true), "id = ?", id);
    }

    @Override
    public void updateName(long id, String name) {
        session.update(new Record(User.TABLE).put(User.NAME, name), "id = ?", id);
    }

    @Override
    public void updateEmail(long id, String email) {
        session.update(new Record(User.TABLE).put(User.EMAIL, email), "id = ?", id);
    }

    @Override
    public void updatePassword(long id, String password) {
        session.update(new Record(User.TABLE).put(User.PASSWORD, password), "id = ?", id);
    }
}
