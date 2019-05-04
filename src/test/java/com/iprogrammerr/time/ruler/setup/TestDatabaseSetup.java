package com.iprogrammerr.time.ruler.setup;

import com.iprogrammerr.time.ruler.Configuration;
import com.iprogrammerr.time.ruler.database.Database;
import com.iprogrammerr.time.ruler.database.SqlDatabase;
import org.h2.tools.RunScript;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;

public class TestDatabaseSetup {

    private final Database database;

    public TestDatabaseSetup(Database database) {
        this.database = database;
    }

    public TestDatabaseSetup(String user, String password, String jdbcUrl) {
        this(new SqlDatabase(user, password, jdbcUrl));
    }

    public TestDatabaseSetup(Configuration configuration) {
        this(configuration.databaseUser(), configuration.databasePassword(), configuration.jdbcUrl());
    }

    public TestDatabaseSetup() {
        this(Configuration.fromClassPath());
    }

    public void setup() {
        try (Connection c = database.connection();
             BufferedReader r = new BufferedReader(
                 new FileReader(TestDatabaseSetup.class.getClassLoader().getResource("./schema.sql").getFile())
             )) {
            RunScript.execute(c, r);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Database database() {
        return database;
    }
}
