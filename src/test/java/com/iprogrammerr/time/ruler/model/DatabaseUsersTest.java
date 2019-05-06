package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.setup.TestDatabaseSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseUsersTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;

    @Before
    public void setup() {
        users = new DatabaseUsers(new SqlDatabaseSession(setup.source(), new QueryTemplates()));
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.close();
    }

    @Test
    public void returnsListOfAll() {
        RandomUsers randomUsers = new RandomUsers();
        User first = randomUsers.user();
        User second = randomUsers.user();
        users.create(first.name, first.email, first.password);
        users.create(second.name, second.email, second.password);
        MatcherAssert.assertThat("Does not contain proper elements", users.all(), Matchers.hasSize(2));
    }

    @Test
    public void returnsEmptyAsAll() {
        MatcherAssert.assertThat("Does not return empty list", users.all(), Matchers.empty());
    }

    @Test
    public void returnsListOfAllInactive() {
        User user = new RandomUsers().user();
        users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat("Does not contain proper elements", users.allInactive(), Matchers.hasSize(1));
    }

    @Test
    public void returnsEmptyAsAllInactive() {
        MatcherAssert.assertThat("Does not return empty list", users.allInactive(), Matchers.empty());
    }

    @Test
    public void updates() {
        RandomUsers randomUsers = new RandomUsers();
        User toCreate = randomUsers.user();
        long id = users.create(toCreate.name, toCreate.email, toCreate.password);
        User toUpdate = randomUsers.user(id);
        users.update(toUpdate);
        MatcherAssert.assertThat("Update failure", users.user(id), Matchers.equalTo(toUpdate));
    }

    @Test
    public void throwsExceptionIfUserWithIdDoesNotExist() {
        long id = 1;
        MatcherAssert.assertThat(
            "Does not throw expected exception",
            () -> users.user(id),
            new ThrowsMatcher(String.format("There is no user with %d id", id))
        );
    }

    @Test
    public void findsUserWithId() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Can not find user with given id",
            users.user(id), Matchers.equalTo(new User(id, user.name, user.email, user.password, false))
        );
    }

    @Test
    public void returnsUserWithEmail() {
        User user = new RandomUsers().user();
        user = user.withId(users.create(user.name, user.email, user.password));
        MatcherAssert.assertThat("Should return user with email", users.withEmail(user.email).get(),
            Matchers.equalTo(user));
    }

    @Test
    public void returnsEmptyIfEmailIsNotUsed() {
        String email = new RandomStrings().email();
        MatcherAssert.assertThat("Should return empty", users.withEmail(email).isPresent(),
            Matchers.equalTo(false));
    }

    @Test
    public void returnsUserWithName() {
        User user = new RandomUsers().user();
        user = user.withId(users.create(user.name, user.email, user.password));
        MatcherAssert.assertThat("Should return user with name", users.withName(user.name).get(),
            Matchers.equalTo(user));
    }

    @Test
    public void returnsEmptyIfNameIsNotUsed() {
        String name = new RandomStrings().alphabetic(5);
        MatcherAssert.assertThat("Should return empty", users.withName(name).isPresent(),
            Matchers.equalTo(false));
    }
}
