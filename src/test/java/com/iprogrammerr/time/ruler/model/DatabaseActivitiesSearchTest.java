package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.setup.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivitiesSearch;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseActivitiesSearchTest {

    private static final int MAX_ACTIVITIES_SIZE = 100;
    private static final int MAX_ACTIVITY_NAME_SIZE = 50;
    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseActivities activities;
    private DatabaseActivitiesSearch activitiesSearch;

    @Before
    public void setup() {
        DatabaseSession session = new SqlDatabaseSession(setup.database(), new QueryTemplates());
        users = new DatabaseUsers(session);
        activities = new DatabaseActivities(session);
        activitiesSearch = new DatabaseActivitiesSearch(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
    }

    @Test
    public void returnsEmptyListOfUserDate() {
        MatcherAssert.assertThat("Is not empty", activitiesSearch.ofUserDate(fakeOrRealUserId(), 1), Matchers.empty());
    }

    private long fakeOrRealUserId() {
        Random random = new Random();
        long id = 1;
        if (random.nextBoolean()) {
            User user = new RandomUsers().user();
            id = users.create(user.name, user.email, user.password);
        }
        return id;
    }

    @Test
    public void returnsEmptyListOfUserDatePlanned() {
        MatcherAssert.assertThat("Is not empty", activitiesSearch.ofUserDatePlanned(fakeOrRealUserId(), 1),
            Matchers.empty());
    }

    @Test
    public void returnsEmptyListOfUserDateDone() {
        MatcherAssert.assertThat("Is not empty", activitiesSearch.ofUserDateDone(fakeOrRealUserId(), 1),
            Matchers.empty());
    }

    @Test
    public void returnsListOfUserDate() {
        RandomUsers randomUsers = new RandomUsers();
        RandomActivities randomActivities = new RandomActivities();
        User user = randomUsers.user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
        insertActivityWithUser(randomUsers, randomActivities, date);

        Activity first = randomActivities.activity(userId, date);
        Activity second = randomActivities.activity(userId, date + 1);
        first = first.withId(activities.create(first));
        second = second.withId(activities.create(second));

        MatcherAssert.assertThat("Does not return proper activities", activitiesSearch.ofUserDate(userId, date),
            Matchers.contains(first, second)
        );
    }

    private long insertActivityWithUser(RandomUsers randomUsers, RandomActivities randomActivities, long date) {
        User user = randomUsers.user();
        long id = users.create(user.name, user.email, user.password);
        return activities.create(randomActivities.activity(id, date));
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
        long date = Instant.now().getEpochSecond();
        insertActivityWithUser(randomUsers, randomActivities, date);

        Activity first = randomActivities.activity(userId, date, !planned);
        first = first.withId(activities.create(first));
        activities.create(randomActivities.activity(userId, date, planned));

        String message;
        List<Activity> userActivities;
        if (planned) {
            message = "Does not return planned activities";
            userActivities = activitiesSearch.ofUserDatePlanned(userId, date);
        } else {
            message = "Does not return done activities";
            userActivities = activitiesSearch.ofUserDateDone(userId, date);
        }
        MatcherAssert.assertThat(message, userActivities, Matchers.contains(first));
    }

    @Test
    public void returnsListOfUserDateDone() {
        returnsListOfUserDate(false);
    }

    @Test
    public void returnsLimitedWithOffset() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
        RandomActivities randomActivities = new RandomActivities(random);

        List<Activity> userActivities = new ArrayList<>();
        int toCreate = 1 + random.nextInt(MAX_ACTIVITIES_SIZE);
        boolean ascending = random.nextBoolean();
        for (int i = 0; i < toCreate; i++) {
            Activity a = randomActivities.activity(userId, ascending ? date + i : date - i);
            userActivities.add(a.withId(activities.create(a)));
        }
        int offset = toCreate / (1 + random.nextInt(toCreate));
        int limit = toCreate - offset;
        MatcherAssert.assertThat("Does not return limited list with offset properly sorted",
            activitiesSearch.userActivities(userId, offset, limit, ascending),
            Matchers.contains(toMatchActivities(offset, userActivities.size(), userActivities)));
    }

    private Activity[] toMatchActivities(int offset, int end, List<Activity> source) {
        return source.subList(offset, end).toArray(new Activity[end - offset]);
    }

    @Test
    public void returnsEmpty() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        MatcherAssert.assertThat("Should return empty",
            activitiesSearch.userActivities(userId, random.nextInt(), random.nextInt(), random.nextBoolean()),
            Matchers.empty());
    }

    @Test
    public void countsMatches() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
        RandomActivities randomActivities = new RandomActivities(random);
        RandomStrings randomStrings = new RandomStrings(random);
        String pattern = randomStrings.alphabetic(1 + random.nextInt(MAX_ACTIVITY_NAME_SIZE));

        int toCreate = 1 + random.nextInt(MAX_ACTIVITIES_SIZE);
        int expectedMatches = 0;
        for (int i = 0; i < toCreate; i++) {
            Activity a = randomActivities.activity(userId, date);
            if (random.nextBoolean()) {
                a = new Activity(a.userId, nameMatchingPattern(pattern, i), a.startDate, a.endDate, a.done);
                expectedMatches++;
            }
            activities.create(a);
        }

        MatcherAssert.assertThat("Does not return expected matches", activitiesSearch.matches(userId, pattern),
            Matchers.equalTo(expectedMatches));
    }

    private String nameMatchingPattern(String pattern, int index) {
        return pattern + "_" + index;
    }

    @Test
    public void returnsLimitedWithOffsetOfPattern() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        long date = Instant.now().getEpochSecond();
        RandomActivities randomActivities = new RandomActivities(random);
        RandomStrings randomStrings = new RandomStrings(random);
        String pattern = randomStrings.alphabetic(1 + random.nextInt(MAX_ACTIVITY_NAME_SIZE));

        int toCreate = 1 + random.nextInt(MAX_ACTIVITIES_SIZE);
        boolean ascending = random.nextBoolean();
        List<Activity> expected = new ArrayList<>();
        boolean match = true;
        for (int i = 0; i < toCreate; i++) {
            Activity a = randomActivities.activity(userId, ascending ? date + i : date - i);
            if (match) {
                a = new Activity(a.userId, nameMatchingPattern(pattern, i), a.startDate, a.endDate, a.done);
            }
            long id = activities.create(a);
            if (match) {
                expected.add(a.withId(id));
            }
            match = random.nextBoolean();
        }
        int offset = random.nextInt(expected.size());
        int limit = expected.size();

        MatcherAssert.assertThat("Does not return limited list with offset properly sorted of pattern",
            activitiesSearch.userActivities(userId, pattern, offset, limit, ascending),
            Matchers.contains(toMatchActivities(offset, expected.size(), expected)));
    }

    @Test
    public void returnsEmptyOfPattern() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        String pattern = new RandomStrings(random).alphabetic(random.nextInt(MAX_ACTIVITY_NAME_SIZE));
        MatcherAssert.assertThat("Should return empty",
            activitiesSearch.userActivities(userId, pattern, random.nextInt(), random.nextInt(), random.nextBoolean()),
            Matchers.empty());
    }
}