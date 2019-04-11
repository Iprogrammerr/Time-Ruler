package com.iprogrammerr.time.ruler.model.day;

import java.sql.ResultSet;
import java.util.Objects;

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

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (this == other) {
            equal = true;
        } else if (other != null && Day.class.isAssignableFrom(other.getClass())) {
            Day day = (Day) other;
            equal = id == day.id && userId == day.userId && date == day.date;
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, date);
    }
}
