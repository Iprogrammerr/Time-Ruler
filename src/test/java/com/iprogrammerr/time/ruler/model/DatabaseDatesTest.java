package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.activity.DatabaseDates;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.setup.TestDatabaseSetup;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DatabaseDatesTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseActivities activities;
    private DatabaseDates dates;

    @Before
    public void setup() {
        DatabaseSession session = new SqlDatabaseSession(setup.source(), new QueryTemplates());
        users = new DatabaseUsers(session);
        activities = new DatabaseActivities(session);
        dates = new DatabaseDates(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.close();
    }

    @Test
    public void returnsZeroAsFirstActivity() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat("Does not return 0", dates.userFirstActivity(userId),
            Matchers.equalTo(0L));
    }

    @Test
    public void returnsUserFirstActivity() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        RandomActivities randomActivities = new RandomActivities(random);
        Activity activity = randomActivities.activity(userId, random.nextLong());
        activities.create(activity);
        activities.create(randomActivities.activity(userId, activity.startDate + 1));
        MatcherAssert.assertThat("Does not return user first activity", dates.userFirstActivity(userId),
            Matchers.equalTo(activity.startDate));
    }

    @Test
    public void returnsEmptyUserPlannedDates() {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long start = new Random().nextInt();
        MatcherAssert.assertThat("Does not return empty list", dates.userPlannedDays(userId, start, start + 1),
            Matchers.empty());
    }

    @Test
    public void returnsUserPlannedDates() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        RandomActivities randomActivities = new RandomActivities(random);
        long firstDate = random.nextInt();
        long secondDate = firstDate + 1;
        long lastDate = firstDate + TimeUnit.DAYS.toSeconds(1);
        activities.create(randomActivities.activity(userId, firstDate));
        activities.create(randomActivities.activity(userId, secondDate));
        activities.create(randomActivities.activity(userId, lastDate));
        MatcherAssert.assertThat("Does not return planned dates", dates.userPlannedDays(userId, firstDate, lastDate),
            Matchers.contains(firstDate, lastDate));
    }
}
