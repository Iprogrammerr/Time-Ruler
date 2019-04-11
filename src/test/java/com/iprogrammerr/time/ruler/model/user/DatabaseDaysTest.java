package com.iprogrammerr.time.ruler.model.user;

import com.iprogrammerr.time.ruler.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.QueryTemplates;
import com.iprogrammerr.time.ruler.database.SqlDatabaseSession;
import com.iprogrammerr.time.ruler.model.day.DatabaseDays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatabaseDaysTest {

    private TestDatabaseSetup setup;
    private DatabaseUsers users;
    private DatabaseDays days;

    @Before
    public void setup() {
        setup = new TestDatabaseSetup();
        DatabaseSession session = new SqlDatabaseSession(setup.database(),  new QueryTemplates());
        users = new DatabaseUsers(session);
        days = new DatabaseDays(session);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.database().close();
    }

    @Test
    public void returnsEmptyUserFromList() {
       long id = 1;
        MatcherAssert.assertThat(
            "Should return an empty list", days.userFrom(id, System.currentTimeMillis()), Matchers.empty()
        );
    }

    @Test
    public void returnsProperUserFromList() {

    }

    @Test
    public void returnsEmptyUserToList() {
        long id = 2;
        MatcherAssert.assertThat(
            "Should return an empty list", days.userTo(id, System.currentTimeMillis()), Matchers.empty()
        );
    }
}
