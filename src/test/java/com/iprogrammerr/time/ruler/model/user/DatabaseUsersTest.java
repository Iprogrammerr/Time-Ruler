package com.iprogrammerr.time.ruler.model.user;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.ThrowsMatcher;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseUsersTest {

    private TestDatabaseSetup setup;
    private DatabaseUsers users;

    @Before
    public void setup() {
        setup = new TestDatabaseSetup();
        users = new DatabaseUsers(new SqlDatabaseSession(setup.database(), new QueryTemplates()));
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
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
    public void returnsTrueIfUserWithEmailExists() {
        User user = new RandomUsers().user();
        users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Should return true with existing user", users.existsWithEmailOrName(user.email), Matchers.equalTo(true)
        );
    }

    @Test
    public void returnsFalseIfUserWithEmailDoesNotExist() {
        String email = new RandomStrings().email();
        MatcherAssert.assertThat(
            "Should return false with non existing user", users.existsWithEmailOrName(email),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void throwsExceptionIfUserWithEmailDoesNotExist() {
        String email = new RandomStrings().email();
        MatcherAssert.assertThat(
            "Does not throw expected exception",
            () -> users.byEmailOrName(email),
            new ThrowsMatcher(String.format("There is no user with %s email", email))
        );
    }

    @Test
    public void findsUserWithEmail() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Can not find user with given email",
            users.byEmailOrName(user.email),
            Matchers.equalTo(new User(id, user.name, user.email, user.password, false))
        );
    }

    @Test
    public void returnsTrueIfUserWithNameExists() {
        User user = new RandomUsers().user();
        users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Should return true with existing user", users.existsWithEmailOrName(user.name), Matchers.equalTo(true)
        );
    }

    @Test
    public void returnsFalseIfUserWithNameDoesNotExist() {
        String name = new RandomStrings().alphabetic(5);
        MatcherAssert.assertThat(
            "Should return false with non existing user", users.existsWithEmailOrName(name), Matchers.equalTo(false)
        );
    }

    @Test
    public void throwsExceptionIfUserWithNameDoesNotExist() {
        String name = new RandomStrings().alphabetic(10);
        MatcherAssert.assertThat(
            "Does not throw expected exception",
            () -> users.byEmailOrName(name),
            new ThrowsMatcher(String.format("There is no user with %s name", name))
        );
    }

    @Test
    public void findsUserWithName() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Can not find user with given name", users.byEmailOrName(user.name),
            Matchers.equalTo(new User(id, user.name, user.email, user.password, false))
        );
    }
}
