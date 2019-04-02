package com.iprogrammerr.time.ruler.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class SqlDatabase implements Database {

    private final HikariDataSource dataSource;

    public SqlDatabase(String username, String password, String jdbcUrl) {
        HikariConfig config = new HikariConfig();
        config.setUsername(username);
        config.setPassword(password);
        config.setJdbcUrl(jdbcUrl);
        dataSource = new HikariDataSource(config);
    }

    @Override
    public Connection connection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
