package com.iprogrammerr.time.ruler.model.user;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.time.ruler.setup.TestDatabaseSetup;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.tool.RandomUsers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class DatabaseUsersActualizationTest {

    private static final int MAX_NAME_SIZE = 25;
    private static final int MAX_PASSWORD_SIZE = 100;
    private final TestDatabaseSetup setup = new TestDatabaseSetup();
    private DatabaseUsers users;
    private DatabaseUsersActualization actualization;

    @Before
    public void setup() {
        QueryFactory factory = new SmartQueryFactory(setup.source());
        users = new DatabaseUsers(factory);
        actualization = new DatabaseUsersActualization(factory);
        setup.setup();
    }

    @After
    public void cleanup() {
        setup.close();
    }

    @Test
    public void activates() {
        User user = new RandomUsers().inactive();
        long id = users.create(user.name, user.email, user.password);
        actualization.activate(id);
        MatcherAssert.assertThat("Should activate an user", users.user(id).active, Matchers.equalTo(true));
    }

    @Test
    public void activatesNoOp() {
        actualization.activate(new Random().nextLong());
    }

    @Test
    public void updatesEmail() {
        User user = new RandomUsers().user();
        long id = users.create(user.name, user.email, user.password);
        String newEmail = new RandomStrings().email();
        actualization.updateEmail(id, newEmail);
        MatcherAssert.assertThat("Should update an email", users.user(id).email, Matchers.equalTo(newEmail));
    }

    @Test
    public void updatesEmailNoOp() {
        actualization.updateEmail(new Random().nextLong(), new RandomStrings().email());
    }

    @Test
    public void updatesName() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long id = users.create(user.name, user.email, user.password);
        String newName = new RandomStrings(random).alphabetic(1 + random.nextInt(MAX_NAME_SIZE));
        actualization.updateName(id, newName);
        MatcherAssert.assertThat("Should update a name", users.user(id).name, Matchers.equalTo(newName));
    }

    @Test
    public void updatesNameNoOp() {
        actualization.updateName(new Random().nextLong(), new RandomStrings().alphabetic());
    }

    @Test
    public void updatesPassword() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        long id = users.create(user.name, user.email, user.password);
        String newPassword = new RandomStrings(random).alphanumeric(1 + random.nextInt(MAX_PASSWORD_SIZE));
        actualization.updatePassword(id, newPassword);
        MatcherAssert.assertThat("Should update a password", users.user(id).password, Matchers.equalTo(newPassword));
    }

    @Test
    public void updatePasswordNoOp() {
        actualization.updatePassword(new Random().nextLong(), new RandomStrings().alphanumeric());
    }

    @Test
    public void updatesPasswordByEmail() {
        Random random = new Random();
        User user = new RandomUsers(random).user();
        users.create(user.name, user.email, user.password);
        String newPassword = new RandomStrings(random).alphanumeric(1 + random.nextInt(MAX_PASSWORD_SIZE));
        actualization.updatePassword(user.email, newPassword);
        MatcherAssert.assertThat("Should update a password", users.withEmail(user.email).get().password,
            Matchers.equalTo(newPassword));
    }
}
