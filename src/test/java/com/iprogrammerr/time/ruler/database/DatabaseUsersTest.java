package com.iprogrammerr.time.ruler.database;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.ThrowsMatcher;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
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
        users.create("Igor", "ceigor@gmail.com", "SECRET");
        users.create("Olek", "olek@super.com", "password2");
        int expectedSize = 2;
        MatcherAssert.assertThat(
            "Does not contain proper elements",
            users.all().size(), Matchers.equalTo(expectedSize)
        );
    }

    @Test
    public void returnsEmptyAsAll() {
        MatcherAssert.assertThat("Does not return empty list", users.all(), Matchers.empty());
    }

    @Test
    public void returnsListOfAllInactive() {
        users.create("Igor", "ceigor32@gmail.com", "SECREdT");
        int expectedSize = 1;
        MatcherAssert.assertThat(
            "Does not contain proper elements",
            users.allInactive().size(), Matchers.equalTo(expectedSize)
        );
    }

    @Test
    public void returnsEmptyAsAllInactive() {
        MatcherAssert.assertThat("Does not return empty list", users.allInactive(), Matchers.empty());
    }

    @Test
    public void updates() {
        long id = users.create("Yegor", "yegor@gmail.com", "lovesSpringSecretly");
        User user = new User(id, "Yegor3", "yegor@gmail.com", "lovesHibernateSecretly", true);
        users.update(user);
        MatcherAssert.assertThat("Update failure", users.user(id), Matchers.equalTo(user));
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
        String name = "Aleks";
        String email = "aleks@gmail.com";
        String password = "lovesJustice";
        long id = users.create(name, email, password);
        User user = new User(id, name, email, password, false);
        MatcherAssert.assertThat("Can not find user with given id", users.user(id), Matchers.equalTo(user));
    }

    @Test
    public void returnsTrueIfUserWithEmailExists() {
        String email = "example@example.com";
        users.create("Name", email, "1234five");
        MatcherAssert.assertThat(
            "Should return true with existing user", users.existsWithEmailOrName(email), Matchers.equalTo(true)
        );
    }

    @Test
    public void returnsFalseIfUserWithEmailDoesNotExist() {
        MatcherAssert.assertThat(
            "Should return false with non existing user", users.existsWithEmailOrName("abc@abc.com"),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void throwsExceptionIfUserWithEmailDoesNotExist() {
        String email = "super@super.com";
        MatcherAssert.assertThat(
            "Does not throw expected exception",
            () -> users.byEmailOrName(email),
            new ThrowsMatcher(String.format("There is no user with %s email", email))
        );
    }

    @Test
    public void findsUserWithEmail() {
        String name = "Aleks";
        String email = "aleks@gmail.com";
        String password = "lovesJustice";
        long id = users.create(name, email, password);
        User user = new User(id, name, email, password, false);
        MatcherAssert.assertThat(
            "Can not find user with given email", users.byEmailOrName(email), Matchers.equalTo(user)
        );
    }

    @Test
    public void returnsTrueIfUserWithNameExists() {
        String name = "Kitty";
        users.create(name, "example@example.com", "1234five");
        MatcherAssert.assertThat(
            "Should return true with existing user", users.existsWithEmailOrName(name), Matchers.equalTo(true)
        );
    }

    @Test
    public void returnsFalseIfUserWithNameDoesNotExist() {
        MatcherAssert.assertThat(
            "Should return false with non existing user", users.existsWithEmailOrName("abc"), Matchers.equalTo(false)
        );
    }

    @Test
    public void throwsExceptionIfUserWithNameDoesNotExist() {
        String name = "super";
        MatcherAssert.assertThat(
            "Does not throw expected exception",
            () -> users.byEmailOrName(name),
            new ThrowsMatcher(String.format("There is no user with %s name", name))
        );
    }

    @Test
    public void findsUserWithName() {
        String name = "Alo";
        String email = "alo@gmail.com";
        String password = "lovesFreedom";
        long id = users.create(name, email, password);
        User user = new User(id, name, email, password, false);
        MatcherAssert.assertThat(
            "Can not find user with given name", users.byEmailOrName(name), Matchers.equalTo(user)
        );
    }
}
