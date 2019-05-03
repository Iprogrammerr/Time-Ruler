package com.iprogrammerr.time.ruler.model.activity;

import java.sql.ResultSet;
import java.util.Objects;

public class Activity {

    public static final String TABLE = "activity";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String NAME = "name";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String DONE = "done";

    public final long id;
    public final long userId;
    public final String name;
    public final long startDate;
    public final long endDate;
    public final boolean done;

    public Activity(long id, long userId, String name, long startTime, long endTime, boolean done) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.startDate = startTime;
        this.endDate = endTime;
        this.done = done;
    }

    public Activity(long userId, String name, long startTime, long endTime, boolean done) {
        this(0, userId, name, startTime, endTime, done);
    }

    public Activity(ResultSet result) throws Exception {
        this(result.getLong(ID), result.getLong(USER_ID), result.getString(NAME), result.getInt(START_DATE),
            result.getInt(END_DATE), result.getBoolean(DONE));
    }

    public Activity withId(long id) {
        return new Activity(id, userId, name, startDate, endDate, done);
    }

    public boolean intersects(Activity other) {
        return (startDate <= other.startDate && endDate >= other.endDate) ||
            (startDate > other.startDate && endDate < other.endDate) ||
            (startDate >= other.startDate && startDate < other.endDate) ||
            (endDate > other.startDate && endDate < other.endDate);
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (this == other) {
            equal = true;
        } else if (other != null && getClass().equals(other.getClass())) {
            Activity activity = (Activity) other;
            equal = id == activity.id && userId == activity.userId && name.equals(activity.name)
                && startDate == activity.startDate && endDate == activity.endDate
                && done == activity.done;
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, startDate, endDate, done);
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + id +
            ", userId=" + userId +
            ", name='" + name + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", done=" + done +
            '}';
    }
}
