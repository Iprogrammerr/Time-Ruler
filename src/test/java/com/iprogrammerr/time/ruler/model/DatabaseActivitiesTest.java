package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.day.DatabaseDays;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DatabaseActivitiesTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseDays days;
    private DatabaseActivities activities;

    @Before
    public void setup() {
        DatabaseSession session = new SqlDatabaseSession(setup.database(), new QueryTemplates());
        users = new DatabaseUsers(session);
        days = new DatabaseDays(session);
        activities = new DatabaseActivities(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
    }

    @Test
    public void returnsEmptyListOfNonExistentUserDate() {
        MatcherAssert.assertThat("Is not empty", activities.ofUserDate(1, 1), Matchers.empty());
    }

    @Test
    public void returnsEmptyListOfNonExistentUserDatePlanned() {
        MatcherAssert.assertThat("Is not empty", activities.ofUserDatePlanned(1, 1), Matchers.empty());
    }

    @Test
    public void returnsEmptyListOfNonExistentUserDateDone() {
        MatcherAssert.assertThat("Is not empty", activities.ofUserDateDone(1, 1), Matchers.empty());
    }

    @Test
    public void returnsEmptyListOfExistentUserEmptyDate() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Is not empty", activities.ofUserDate(id, System.currentTimeMillis()), Matchers.empty()
        );
    }

    @Test
    public void returnsEmptyListOfExistentUserEmptyDatePlanned() {
        returnsEmptyListOfExistentUserEmptyDate(true);
    }

    private void returnsEmptyListOfExistentUserEmptyDate(boolean planned) {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Is not empty",
            planned ? activities.ofUserDatePlanned(id, System.currentTimeMillis()) :
                activities.ofUserDateDone(id, System.currentTimeMillis()),
            Matchers.empty()
        );
    }

    @Test
    public void returnsEmptyListOfExistentUserEmptyDateDone() {
        returnsEmptyListOfExistentUserEmptyDate(false);
    }

    @Test
    public void returnsListOfUserDate() {
        RandomUsers randomUsers = new RandomUsers();
        RandomActivities randomActivities = new RandomActivities();
        User user = randomUsers.user();
        long userId = users.create(user.name, user.email, user.password);
        long date = System.currentTimeMillis();
        long dayId = days.createForUser(userId, date);
        insertUserWithActivity(randomUsers, randomActivities, date);

        Activity first = randomActivities.activity(dayId);
        Activity second = randomActivities.activity(dayId);
        first = first.withId(activities.create(first));
        second = second.withId(activities.create(second));

        MatcherAssert.assertThat(
            "Does not return proper activities", activities.ofUserDate(userId, date),
            Matchers.contains(first, second)
        );
    }

    private void insertUserWithActivity(RandomUsers randomUsers, RandomActivities randomActivities, long date) {
        User user = randomUsers.user();
        long id = users.create(user.name, user.email, user.password);
        activities.create(randomActivities.activity(days.createForUser(id, date)));
    }

    @Test
    public void returnsListOfUserDatePlanned() {
        returnsListOfUserDate(true);
    }

    private void returnsListOfUserDate(boolean planned) {
        RandomUsers randomUsers = new RandomUsers();
        RandomActivities randomActivities = new RandomActivities();
        User user = randomUsers.user();
        long userId = users.create(user.name, user.email, user.password);
        long date = System.currentTimeMillis();
        long dayId = days.createForUser(userId, date);
        insertUserWithActivity(randomUsers, randomActivities, date);

        Activity first = randomActivities.activity(dayId, !planned);
        first = first.withId(activities.create(first));
        activities.create(randomActivities.activity(dayId, planned));

        String message;
        List<Activity> userActivities;
        if (planned) {
            message = "Does not return planned activities";
            userActivities = activities.ofUserDatePlanned(userId, date);
        } else {
            message = "Does not return done activities";
            userActivities = activities.ofUserDateDone(userId, date);
        }
        MatcherAssert.assertThat(message, userActivities, Matchers.contains(first));
    }

    @Test
    public void returnsListOfUserDateDone() {
        returnsListOfUserDate(false);
    }
}
