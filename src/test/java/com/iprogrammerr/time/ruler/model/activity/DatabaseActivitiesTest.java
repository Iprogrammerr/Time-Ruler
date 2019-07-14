package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
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

import java.time.Instant;
import java.util.Random;

public class DatabaseActivitiesTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseActivities activities;

    @Before
    public void setup() {
        DatabaseSession session = new SqlDatabaseSession(setup.source(), new QueryTemplates());
        QueryFactory factory = new SmartQueryFactory(setup.source());
        users = new DatabaseUsers(session);
        activities = new DatabaseActivities(factory);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.close();
    }

    @Test
    public void returnsActivityFromId() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        Activity activity = new RandomActivities(random).activity(userId, Instant.now().getEpochSecond());
        long activityId = activities.create(activity);
        MatcherAssert.assertThat("Should return newly created activity", activity.withId(activityId),
            Matchers.equalTo(activities.activity(activityId).get()));
    }

    @Test
    public void returnsEmptyFromNonExistentId() {
        MatcherAssert.assertThat("Does not return empty activity",
            activities.activity(new Random().nextLong()).isPresent(), Matchers.equalTo(false));
    }

    @Test
    public void updatesActivity() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        RandomActivities randomActivities = new RandomActivities(random);
        long userId = users.create(user.name, user.email, user.password);
        long activityId = activities.create(randomActivities.activity(userId));
        Activity toUpdate = randomActivities.activity(userId, Instant.now().getEpochSecond()).withId(activityId);
        activities.update(toUpdate);
        MatcherAssert.assertThat("Does not update", toUpdate,
            Matchers.equalTo(activities.activity(activityId).get()));
    }

    @Test
    public void deletesActivity() {
        long id = insertActivityWithUser();
        activities.delete(id);
        MatcherAssert.assertThat("Should delete activity", activities.activity(id).isPresent(),
            Matchers.equalTo(false));
    }

    private long insertActivityWithUser(RandomUsers randomUsers, RandomActivities randomActivities, long date) {
        User user = randomUsers.user();
        long id = users.create(user.name, user.email, user.password);
        return activities.create(randomActivities.activity(id, date));
    }

    private long insertActivityWithUser() {
        return insertActivityWithUser(new RandomUsers(), new RandomActivities(), Instant.now().getEpochSecond());
    }

    @Test
    public void returnsTrueIfBelongsToUser() {
        belongsToUser(true);
    }

    private void belongsToUser(boolean belongs) {
        User user = new RandomUsers().user();
        long userId = users.create(user.name, user.email, user.password);
        long activityId = activities.create(new RandomActivities().activity(userId, Instant.now().getEpochSecond()));
        long toTestId = belongs ? userId : userId + 1;
        MatcherAssert.assertThat(String.format("Should return %b", belongs),
            activities.belongsToUser(toTestId, activityId), Matchers.equalTo(belongs));
    }

    @Test
    public void returnsFalseIfDoesNotBelongsToUser() {
        belongsToUser(false);
    }

    @Test
    public void setsActivityDone() {
        long id = insertActivityWithUser();
        boolean done = !activities.activity(id).get().done;
        activities.setDone(id, done);
        MatcherAssert.assertThat("Does not activate done status", activities.activity(id).get().done,
            Matchers.equalTo(done));
    }
}
