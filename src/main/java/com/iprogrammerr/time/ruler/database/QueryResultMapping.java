package com.iprogrammerr.time.ruler.database;

import java.sql.ResultSet;

public interface QueryResultMapping<T> {
    T map(ResultSet result) throws Exception;
}
