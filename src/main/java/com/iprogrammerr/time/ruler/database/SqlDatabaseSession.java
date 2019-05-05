package com.iprogrammerr.time.ruler.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlDatabaseSession implements DatabaseSession {

    private final DataSource source;
    private final QueryTemplates templates;

    public SqlDatabaseSession(DataSource source, QueryTemplates templates) {
        this.source = source;
        this.templates = templates;
    }

    @Override
    public <T> T select(QueryResultMapping<T> mapping, String template, Object... values) {
        return select(mapping, templates.query(template, values));
    }

    @Override
    public <T> T select(QueryResultMapping<T> mapping, String query) {
        try (Connection c = source.getConnection();
             Statement s = c.createStatement()) {
            ResultSet resultSet = s.executeQuery(query);
            return mapping.map(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long create(Record record) {
        try (Connection c = source.getConnection();
             PreparedStatement s = c.prepareStatement(templates.insert(record),
                 Statement.RETURN_GENERATED_KEYS)) {
            s.executeUpdate();
            ResultSet resultSet = s.getGeneratedKeys();
            resultSet.next();
            return resultSet.getLong(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Record record, String whereTemplate, Object... values) {
        try (Connection c = source.getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate(templates.update(record, whereTemplate, values));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String table, String whereTemplate, Object... values) {
        try (Connection c = source.getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate(templates.delete(table, whereTemplate, values));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
