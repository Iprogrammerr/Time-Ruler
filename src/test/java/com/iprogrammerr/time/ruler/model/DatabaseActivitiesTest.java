package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
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
        DatabaseSession session = new SqlDatabaseSession(setup.database(), new QueryTemplates());
        users = new DatabaseUsers(session);
        activities = new DatabaseActivities(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
    }


    @Test
    public void returnsActivityFromId() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        Activity activity = new RandomActivities(random).activity(userId, Instant.now().getEpochSecond());
        long activityId = activities.create(activity);
        MatcherAssert.assertThat("Should return newly created activity", activity.withId(activityId),
            Matchers.equalTo(activities.activity(activityId)));
    }

    @Test
    public void throwsExceptionIfThereIsNoActivityWithGivenId() {
        long id = new Random().nextLong();
        String message = String.format("There is no activity associated with %d id", id);
        MatcherAssert.assertThat("Does not throw exception with proper message", () -> activities.activity(id),
            new ThrowsMatcher(message));
    }

    @Test
    public void deletesActivity() {
        long id = insertActivityWithUser();
        activities.delete(id);
        MatcherAssert.assertThat("Should delete activity", activities.exists(id), Matchers.equalTo(false));
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
        boolean done = !activities.activity(id).done;
        activities.setDone(id, done);
        MatcherAssert.assertThat("Does not update done status", activities.activity(id).done,
            Matchers.equalTo(done));
    }
}
