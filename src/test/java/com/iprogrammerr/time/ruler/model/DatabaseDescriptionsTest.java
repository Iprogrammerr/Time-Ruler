package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import com.iprogrammerr.time.ruler.mock.RandomUsers;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DatabaseActivities;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.day.DatabaseDays;
import com.iprogrammerr.time.ruler.model.description.DatabaseDescriptions;
import com.iprogrammerr.time.ruler.model.description.Description;
import com.iprogrammerr.time.ruler.model.user.DatabaseUsers;
import com.iprogrammerr.time.ruler.model.user.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class DatabaseDescriptionsTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseDays days;
    private DatabaseActivities activities;
    private DatabaseDescriptions descriptions;

    @Before
    public void setup() {
        DatabaseSession session = new SqlDatabaseSession(setup.database(), new QueryTemplates());
        users = new DatabaseUsers(session);
        days = new DatabaseDays(session);
        activities = new DatabaseActivities(session);
        descriptions = new DatabaseDescriptions(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
    }

    @Test
    public void createsDescription() {
        String content = new RandomStrings().alphabetic();
        long id = descriptions.create(new Description(createActivity().id, content));
        MatcherAssert.assertThat("Does not return proper id", id, Matchers.greaterThan(0L));
    }

    private Activity createActivity() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long userId = users.create(user.name, user.email, user.password);
        long dayId = days.createForUser(userId, random.nextInt());
        Activity activity = new RandomActivities(random).activity(dayId);
        return activity.withId(activities.create(activity));
    }

    @Test
    public void returnsDescribedActivity() {
        Activity activity = createActivity();
        String content = new RandomStrings().alphabetic();
        descriptions.create(new Description(activity.id, content));
        MatcherAssert.assertThat("Does not return newly created DescribedActivity",
            descriptions.describedActivity(activity.id), Matchers.equalTo(new DescribedActivity(activity, content)));
    }

}