package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

public class DayActivity {

    public final long id;
    public final String name;
    public final String start;
    public final String end;
    public final boolean done;

    public DayActivity(long id, String name, String start, String end, boolean done) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.done = done;
    }

    public DayActivity(Activity activity, DateTimeFormatting formatting, Function<Long, Instant> timeTransformation) {
        this(activity.id, activity.name, formatting.time(timeTransformation.apply(activity.startDate)),
            formatting.time(timeTransformation.apply(activity.endDate)), activity.done);
    }

    @Override
    public boolean equals(Object o) {
        boolean equal;
        if (o == this) {
            equal = true;
        } else if (o != null && o.getClass().isAssignableFrom(DayActivity.class)) {
            DayActivity other = (DayActivity) o;
            equal = id == other.id && name.equals(other.name) && start.equals(other.start) && end.equals(other.end)
                && done == other.done;
        } else {
            equal = false;
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, start, end, done);
    }
}
