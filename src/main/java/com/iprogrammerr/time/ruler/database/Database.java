package com.iprogrammerr.time.ruler.database;

import java.sql.Connection;

public interface Database {

    Connection connection();

    void close();
}
