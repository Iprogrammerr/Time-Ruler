package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
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

    @Test
    public void returnsActivityWithEmptyDescription() {
        Activity activity = createActivity();
        MatcherAssert.assertThat("Does not return activity with empty description",
            descriptions.describedActivity(activity.id), Matchers.equalTo(new DescribedActivity(activity, "")));
    }

    @Test
    public void throwsExceptionIfActivityDoesNotExist() {
        long id = new Random().nextInt();
        String message = String.format("There is no activity associated with %d id", id);
        MatcherAssert.assertThat("Does no throw exception with proper message",
            () -> descriptions.describedActivity(id), new ThrowsMatcher(message));
    }

    @Test
    public void updatesDescriptionIfExists() {
        Activity activity = createActivity();
        Random random = new Random();
        RandomStrings strings = new RandomStrings(random);
        Description description = new Description(activity.id, strings.alphabetic());
        descriptions.create(description);
        description = new Description(activity.id,
            description.content.substring(0, 1 + random.nextInt(description.content.length())));
        descriptions.updateOrCreate(description);
        MatcherAssert.assertThat("Does not update description", description.content,
            Matchers.equalTo(descriptions.describedActivity(activity.id).description));
    }

    @Test
    public void createsDescriptionIfDoesNotExist() {
        Activity activity = createActivity();
        Description description = new Description(activity.id, new RandomStrings().alphabetic());
        descriptions.updateOrCreate(description);
        MatcherAssert.assertThat("Does not create description", description.content,
            Matchers.equalTo(descriptions.describedActivity(activity.id).description));
    }
}
