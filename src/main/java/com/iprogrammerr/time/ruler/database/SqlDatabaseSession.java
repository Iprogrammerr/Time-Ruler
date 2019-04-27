package com.iprogrammerr.time.ruler.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlDatabaseSession implements DatabaseSession {

    private final Database database;
    private final QueryTemplates templates;

    public SqlDatabaseSession(Database database, QueryTemplates templates) {
        this.database = database;
        this.templates = templates;
    }

    @Override
    public <T> T select(QueryResultMapping<T> mapping, String template, Object... values) {
        return select(mapping, templates.query(template, values));
    }

    @Override
    public <T> T select(QueryResultMapping<T> mapping, String query) {
        try (Connection c = database.connection()) {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return mapping.map(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long create(Record record) {
        try (Connection c = database.connection()) {
            PreparedStatement preparedStatement = c.prepareStatement(
                templates.insert(record), Statement.RETURN_GENERATED_KEYS
            );
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Record record, String whereTemplate, Object... values) {
        try (Connection c = database.connection()) {
            Statement statement = c.createStatement();
            statement.executeUpdate(templates.update(record, whereTemplate, values));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String table, String whereTemplate, Object... values) {
        try (Connection c = database.connection()) {
            Statement statement = c.createStatement();
            statement.executeUpdate(templates.delete(table, whereTemplate, values));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
