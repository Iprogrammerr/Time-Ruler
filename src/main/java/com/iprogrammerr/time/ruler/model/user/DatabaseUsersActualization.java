package com.iprogrammerr.time.ruler.model.user;

import com.iprogrammerr.smart.query.QueryFactory;

public class DatabaseUsersActualization implements UsersActualization {

    private final QueryFactory factory;

    public DatabaseUsersActualization(QueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public void activate(long id) {
        factory.newQuery().dsl()
            .update(User.TABLE).set(User.ACTIVE, true)
            .where(User.ID).equal().value(id)
            .query()
            .execute();
    }

    @Override
    public void updateName(long id, String name) {
        factory.newQuery().dsl()
            .update(User.TABLE).set(User.NAME, name)
            .where(User.ID).equal().value(id)
            .query().execute();
    }

    @Override
    public void updateEmail(long id, String email) {
        factory.newQuery().dsl()
            .update(User.TABLE).set(User.EMAIL, email)
            .where(User.ID).equal().value(id)
            .query().execute();
    }

    @Override
    public void updatePassword(long id, String password) {
        factory.newQuery().dsl()
            .update(User.TABLE).set(User.PASSWORD, password)
            .where(User.ID).equal().value(id)
            .query().execute();
    }

    @Override
    public void updatePassword(String email, String password) {
        factory.newQuery().dsl()
            .update(User.TABLE).set(User.PASSWORD, password)
            .where(User.EMAIL).equal().value(email)
            .query().execute();
    }
}
