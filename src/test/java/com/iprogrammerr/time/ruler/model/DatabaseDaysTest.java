package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.day.DatabaseDays;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class DatabaseDaysTest {

    private TestDatabaseSetup setup;
    private DatabaseUsers users;
    private DatabaseDays days;

    @Before
    public void setup() {
        setup = new TestDatabaseSetup();
        DatabaseSession session = new SqlDatabaseSession(setup.database(), new QueryTemplates());
        users = new DatabaseUsers(session);
        days = new DatabaseDays(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
    }

    @Test
    public void returnsEmptyUserFromList() {
        long id = 1;
        MatcherAssert.assertThat(
            "Should return an empty list", days.userFrom(id, System.currentTimeMillis()), Matchers.empty()
        );
    }

    @Test
    public void returnsProperUserFromList() {
        returnsProperUserList(true);
    }

    private void returnsProperUserList(boolean from) {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        long firstDate = System.currentTimeMillis();
        long secondDate = firstDate + TimeUnit.DAYS.toMillis(1);
        long firstId = days.createForUser(id, firstDate);
        long secondId = days.createForUser(id, secondDate);
        MatcherAssert.assertThat(
            "Should return a list that contains expected elements",
            from ? days.userFrom(id, firstDate) : days.userTo(id, secondDate),
            Matchers.contains(new Day(firstId, id, firstDate), new Day(secondId, id, secondDate))
        );
    }

    @Test
    public void returnsEmptyUserToList() {
        long id = 2;
        MatcherAssert.assertThat(
            "Should return an empty list", days.userTo(id, System.currentTimeMillis()), Matchers.empty()
        );
    }

    @Test
    public void returnsProperUserToList() {
        returnsProperUserList(false);
    }

    @Test
    public void createsForUser() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long date = System.currentTimeMillis();
        long dayId = days.createForUser(userId, date);
        MatcherAssert.assertThat(
            "Created day should be equal to expected", days.userFrom(userId, date),
            Matchers.contains(new Day(dayId, userId, date))
        );
    }

    @Test
    public void returnsFalseOfUserDayIfEmpty() {
        MatcherAssert.assertThat(
            "Should return false if asked about non existing day", days.ofUserExists(1, System.currentTimeMillis()),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void returnsFalseOfAnotherUserDayIfExists() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long date = System.currentTimeMillis();
        days.createForUser(userId, date);
        MatcherAssert.assertThat(
            "Should return false if asked about existing another user day", days.ofUserExists(userId + 1, date),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void returnsTrueOfUserDayIfExists() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long date = System.currentTimeMillis();
        days.createForUser(userId, date);
        MatcherAssert.assertThat(
            "Should return true if asked about existing day", days.ofUserExists(userId, date),
            Matchers.equalTo(true)
        );
    }
}
