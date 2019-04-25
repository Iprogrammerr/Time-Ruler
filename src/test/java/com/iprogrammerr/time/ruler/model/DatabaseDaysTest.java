package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
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

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DatabaseDaysTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseDays days;

    @Before
    public void setup() {
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
    public void returnsEmptyUserRangeList() {
        long id = 1;
        long from = Instant.now().getEpochSecond();
        MatcherAssert.assertThat(
            "Should return an empty list", days.userRange(id, from, from + 1), Matchers.empty()
        );
    }

    @Test
    public void returnsProperUserRangeList() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        long firstDate = Instant.now().getEpochSecond();
        long secondDate = firstDate + TimeUnit.DAYS.toSeconds(1);
        long firstId = days.createForUser(id, firstDate);
        long secondId = days.createForUser(id, secondDate);
        MatcherAssert.assertThat("Should return a list that contains expected elements",
            days.userRange(id, firstDate, secondDate),
            Matchers.contains(new Day(firstId, id, firstDate), new Day(secondId, id, secondDate)));
    }

    @Test
    public void createsForUser() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
        long dayId = days.createForUser(userId, date);
        MatcherAssert.assertThat(
            "Created day should be equal to expected", days.ofUser(userId, date),
            Matchers.equalTo(new Day(dayId, userId, date))
        );
    }

    @Test
    public void returnsFalseOfUserDayIfEmpty() {
        MatcherAssert.assertThat(
            "Should return false if asked about non existing day", days.ofUserExists(1, Instant.now().getEpochSecond()),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void returnsFalseOfAnotherUserDayIfExists() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
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
        long date = Instant.now().getEpochSecond();
        days.createForUser(userId, date);
        MatcherAssert.assertThat(
            "Should return true if asked about existing day", days.ofUserExists(userId, date),
            Matchers.equalTo(true)
        );
    }

    @Test
    public void returnsZeroOfNonExistingUserFirstDate() {
        MatcherAssert.assertThat(
            "Should return negative date", days.userFirstDate(new Random().nextInt()), Matchers.equalTo(0L)
        );
    }

    @Test
    public void returnsUserFirstDate() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
        long min = date - (1 + random.nextInt((int) TimeUnit.DAYS.toSeconds(1)));
        days.createForUser(userId, date);
        days.createForUser(userId, min);
        MatcherAssert.assertThat("Should return min value", days.userFirstDate(userId), Matchers.equalTo(min));
    }

    @Test
    public void returnsFromUserDate() {
        User user = new RandomUsers().user();
        Day day = new Day(users.create(user.name, user.email, user.password), Instant.now().getEpochSecond());
        long dayId = days.createForUser(day.userId, day.date);
        MatcherAssert.assertThat("Should return newly created day", days.ofUser(dayId, day.date),
            Matchers.equalTo(new Day(dayId, day.userId, day.date)));
    }

    @Test
    public void throwsExceptionIfThereIsNoUserWithGivenDate() {
        Random random = new Random();
        long id = random.nextInt();
        long date = Instant.now().getEpochSecond();
        String message = String.format("There is no day associated with %d user and %d date", id, date);
        MatcherAssert.assertThat("Does not throw exception with proper message", () -> days.ofUser(id, date),
            new ThrowsMatcher(message));
    }
}
