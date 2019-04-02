package com.iprogrammerr.time.ruler.database;

public interface DatabaseSession {

    <T> T select(QueryResultMapping<T> mapping, String template, Object... values);

    <T> T select(QueryResultMapping<T> mapping, String query);

    long create(Record record);

    void update(Record record, String whereTemplate, Object... values);

    void delete(String query);
}
