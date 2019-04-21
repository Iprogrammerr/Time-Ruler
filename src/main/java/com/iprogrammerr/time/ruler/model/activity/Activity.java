package com.iprogrammerr.time.ruler.model.activity;

import java.sql.ResultSet;
import java.util.Objects;

public class Activity {

    public static final String TABLE = "activity";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DAY_ID = "day_id";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String DONE = "done";

    public final long id;
    public final String name;
    public final long dayId;
    public final int startTime;
    public final int endTime;
    public final boolean done;

    public Activity(long id, String name, long dayId, int startTime, int endTime, boolean done) {
        this.id = id;
        this.name = name;
        this.dayId = dayId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.done = done;
    }

    public Activity(String name, long dayId, int startTime, int endTime, boolean done) {
        this(0, name, dayId, startTime, endTime, done);
    }

    public Activity(ResultSet result) throws Exception {
        this(
            result.getLong(ID), result.getString(NAME), result.getLong(DAY_ID),
            result.getInt(START_TIME), result.getInt(END_TIME), result.getBoolean(DONE)
        );
    }

    public Activity withId(long id) {
        return new Activity(id, name, dayId, startTime, endTime, done);
    }

    public boolean intersects(Activity other) {
        //TODO implementation
        return false;
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (this == other) {
            equal = true;
        } else if (other != null && getClass().equals(other.getClass())) {
            Activity activity = (Activity) other;
            equal = id == activity.id && name.equals(activity.name) && dayId == activity.dayId &&
                startTime == activity.startTime && endTime == activity.endTime && done == activity.done;
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dayId, startTime, endTime, done);
    }
}
