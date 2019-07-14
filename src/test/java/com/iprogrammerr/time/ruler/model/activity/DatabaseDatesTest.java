package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.setup.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import com.iprogrammerr.time.ruler.tool.RandomUsers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DatabaseDatesTest {

    private static final int DAY_SECONDS = (int) TimeUnit.DAYS.toSeconds(1);
    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseActivities activities;
    private DatabaseDates dates;

    @Before
    public void setup() {
        QueryFactory factory = new SmartQueryFactory(setup.source());
        users = new DatabaseUsers(factory);
        activities = new DatabaseActivities(factory);
        dates = new DatabaseDates(factory);
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
        RandomUsers randomUsers = new RandomUsers(random);
        User user = randomUsers.user();
        long userId = users.create(user.name, user.email, user.password);
        User otherUser = randomUsers.different(user.email, user.name);
        long otherUserId = users.create(otherUser.name, otherUser.email, otherUser.password);

        RandomActivities randomActivities = new RandomActivities(random);
        long firstDate = random.nextInt();
        long secondDate = firstDate + 1;
        long lastDate = secondDate + DAY_SECONDS;
        long afterLastDate = lastDate + DAY_SECONDS;

        activities.create(randomActivities.activity(userId, firstDate));
        activities.create(randomActivities.activity(userId, secondDate));
        activities.create(randomActivities.activity(userId, lastDate));
        activities.create(randomActivities.activity(otherUserId, afterLastDate));

        MatcherAssert.assertThat("Does not return planned dates",
            dates.userPlannedDays(userId, firstDate, afterLastDate),
            Matchers.contains(firstDate, lastDate));
    }
}
