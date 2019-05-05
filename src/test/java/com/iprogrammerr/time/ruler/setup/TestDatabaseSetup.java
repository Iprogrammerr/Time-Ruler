package com.iprogrammerr.time.ruler.setup;

import com.iprogrammerr.time.ruler.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;

public class TestDatabaseSetup {

    private final HikariDataSource source;

    public TestDatabaseSetup(HikariDataSource source) {
        this.source = source;
    }

    public TestDatabaseSetup(String user, String password, String jdbcUrl) {
        this(source(user, password, jdbcUrl));
    }

    public TestDatabaseSetup(Configuration configuration) {
        this(configuration.databaseUser(), configuration.databasePassword(), configuration.jdbcUrl());
    }

    public TestDatabaseSetup() {
        this(Configuration.fromClassPath());
    }

    private static HikariDataSource source(String user, String password, String jdbcUrl) {
        HikariConfig config = new HikariConfig();
        config.setUsername(user);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        return new HikariDataSource(config);
    }

    public void setup() {
        try (Connection c = source.getConnection();
             BufferedReader r = new BufferedReader(new FileReader(
                 TestDatabaseSetup.class.getClassLoader().getResource("./schema.sql").getFile()))) {
            RunScript.execute(c, r);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public DataSource source() {
        return source;
    }

    public void close() {
        source.close();
    }
}
