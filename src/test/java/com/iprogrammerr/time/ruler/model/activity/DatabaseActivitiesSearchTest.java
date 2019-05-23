package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.setup.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.tool.RandomUsers;
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
        DatabaseSession session = new SqlDatabaseSession(setup.source(), new QueryTemplates());
        users = new DatabaseUsers(session);
        activities = new DatabaseActivities(session);
        activitiesSearch = new DatabaseActivitiesSearch(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.close();
    }

    @Test
    public void returnsEmptyUserDayActivities() {
        MatcherAssert.assertThat("Is not empty", activitiesSearch.userDayActivities(fakeOrRealUserId(),
            1), Matchers.empty());
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
    public void returnsEmptyUserDayPlannedActivities() {
        MatcherAssert.assertThat("Is not empty", activitiesSearch.userDayPlannedActivities(fakeOrRealUserId(), 1),
            Matchers.empty());
    }

    @Test
    public void returnsEmptyUserDayDoneActivities() {
        MatcherAssert.assertThat("Is not empty", activitiesSearch.userDayDoneActivities(fakeOrRealUserId(), 1),
            Matchers.empty());
    }

    @Test
    public void returnsUserDayActivities() {
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

        MatcherAssert.assertThat("Does not return proper activities", activitiesSearch.userDayActivities(userId, date),
            Matchers.contains(first, second)
        );
    }

    private long insertActivityWithUser(RandomUsers randomUsers, RandomActivities randomActivities, long date) {
        User user = randomUsers.user();
        long id = users.create(user.name, user.email, user.password);
        return activities.create(randomActivities.activity(id, date));
    }

    @Test
    public void returnsUserDayPlannedActivities() {
        returnsUserDayActivities(true);
    }

    private void returnsUserDayActivities(boolean planned) {
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
            userActivities = activitiesSearch.userDayPlannedActivities(userId, date);
        } else {
            message = "Does not return done activities";
            userActivities = activitiesSearch.userDayDoneActivities(userId, date);
        }
        MatcherAssert.assertThat(message, userActivities, Matchers.contains(first));
    }

    @Test
    public void returnsUserDayDoneActivities() {
        returnsUserDayActivities(false);
    }

    @Test
    public void returnsLimitedWithOffsetActivities() {
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
        int offset = random.nextInt(toCreate);
        int limit = toCreate - offset;
        MatcherAssert.assertThat("Does not return limited list with offset properly sorted",
            activitiesSearch.userActivities(userId, offset, limit, ascending),
            Matchers.contains(toMatchActivities(offset, userActivities.size(), userActivities)));
    }

    private Activity[] toMatchActivities(int offset, int end, List<Activity> source) {
        return source.subList(offset, end).toArray(new Activity[end - offset]);
    }

    @Test
    public void returnsEmptyActivities() {
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
        RandomActivities randomActivities = new RandomActivities(random);
        RandomStrings randomStrings = new RandomStrings(random);
        String pattern = randomStrings.alphabetic(1 + random.nextInt(MAX_ACTIVITY_NAME_SIZE));

        int toCreate = 1 + random.nextInt(MAX_ACTIVITIES_SIZE);
        int expectedMatches = 0;
        for (int i = 0; i < toCreate; i++) {
            Activity a = notMatching(randomActivities, userId, pattern);
            if (random.nextBoolean()) {
                a = new Activity(a.userId, nameMatchingPattern(pattern, i), a.startDate, a.endDate, a.done);
                expectedMatches++;
            }
            activities.create(a);
        }

        MatcherAssert.assertThat("Does not return expected matches", activitiesSearch.matches(userId, pattern),
            Matchers.equalTo(expectedMatches));
    }

    private Activity notMatching(RandomActivities random, long userId, String pattern) {
        Activity activity = random.activity(userId);
        while (activity.name.toLowerCase().startsWith(pattern.toLowerCase())) {
            activity = random.activity(userId);
        }
        return activity;
    }

    private String nameMatchingPattern(String pattern, int index) {
        return pattern + "_" + index;
    }

    @Test
    public void returnsLimitedWithOffsetOfPatternActivities() {
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
    public void returnsEmptyOfPatternActivities() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        String pattern = new RandomStrings(random).alphabetic(random.nextInt(MAX_ACTIVITY_NAME_SIZE));
        MatcherAssert.assertThat("Should return empty",
            activitiesSearch.userActivities(userId, pattern, random.nextInt(), random.nextInt(), random.nextBoolean()),
            Matchers.empty());
    }
}
