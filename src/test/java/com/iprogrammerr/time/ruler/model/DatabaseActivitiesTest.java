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
    public void returnsEmptyListOfExistentUserEmptyDate() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat(
            "Is not empty", activities.ofUserDate(id, System.currentTimeMillis()), Matchers.empty()
        );
    }

    @Test
    public void returnsListOfUserDate() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long date = System.currentTimeMillis() / 1000;
        long dayId = days.createForUser(userId, date);

        RandomActivities randomActivities = new RandomActivities();
        Activity first = randomActivities.activity(dayId);
        Activity second = randomActivities.activity(dayId);
        first = first.withId(activities.create(first));
        second = second.withId(activities.create(second));

        MatcherAssert.assertThat(
            "Does not return proper activities",
            activities.ofUserDate(userId, date),
            Matchers.contains(first, second)
        );
    }

    @Test
    public void createsActivity() {

    }
}
