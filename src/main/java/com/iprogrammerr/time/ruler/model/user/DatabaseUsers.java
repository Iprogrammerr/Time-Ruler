package com.iprogrammerr.time.ruler.model.user;

import com.iprogrammerr.smart.query.QueryFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseUsers implements Users {

    private final QueryFactory factory;

    public DatabaseUsers(QueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<User> all() {
        return factory.newQuery().dsl()
            .selectAll().from(User.TABLE)
            .query()
            .fetch(this::users);
    }

    private List<User> users(ResultSet result) throws Exception {
        List<User> users = new ArrayList<>();
        while (result.next()) {
            users.add(new User(result));
        }
        return users;
    }

    @Override
    public List<User> allInactive() {
        return factory.newQuery().dsl()
            .selectAll().from(User.TABLE).where(User.ACTIVE).equal().value(0)
            .query()
            .fetch(this::users);
    }

    @Override
    public long create(String name, String email, String password) {
        return factory.newQuery().dsl()
            .insertInto(User.TABLE)
            .columns(User.NAME, User.EMAIL, User.PASSWORD)
            .values(name, email, password)
            .query()
            .executeReturningId();
    }

    @Override
    public User user(long id) {
        return factory.newQuery().dsl()
            .selectAll().from(User.TABLE).where(User.ID).equal().value(id)
            .query()
            .fetch(r -> mapOrThrow(r, "There is no user with %s id", String.valueOf(id)));
    }

    private User mapOrThrow(ResultSet result, String exceptionTemplate, String identifier) throws Exception {
        if (result.next()) {
            return new User(result);
        }
        throw new RuntimeException(String.format(exceptionTemplate, identifier));
    }

    @Override
    public Optional<User> withEmail(String email) {
        return factory.newQuery().dsl()
            .selectAll().from(User.TABLE).where(User.EMAIL).equal().value(email)
            .query()
            .fetch(this::userFromResult);
    }

    private Optional<User> userFromResult(ResultSet result) throws Exception {
        if (result.next()) {
            return Optional.of(new User(result));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> withName(String name) {
        return factory.newQuery().dsl()
            .selectAll().from(User.TABLE).where(User.NAME).equal().value(name)
            .query()
            .fetch(this::userFromResult);
    }
}
