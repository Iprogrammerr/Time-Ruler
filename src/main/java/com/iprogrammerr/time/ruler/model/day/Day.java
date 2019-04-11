package com.iprogrammerr.time.ruler.model.day;

import java.sql.ResultSet;

public class Day {

    public static final String TABLE = "day";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String DATE = "date";

    public final long id;
    public final long userId;
    public final long date;

    public Day(long id, long userId, long date) {
        this.id = id;
        this.userId = userId;
        this.date = date;
    }

    public Day(ResultSet result) throws Exception {
        this(result.getLong(ID), result.getLong(USER_ID), result.getLong(DATE));
    }
}
