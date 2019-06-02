package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

public class FoundActivity {

    public final long id;
    public final String date;
    public final String name;

    public FoundActivity(long id, String date, String name) {
        this.id = id;
        this.date = date;
        this.name = name;
    }

    public FoundActivity(Activity activity, DateTimeFormatting formatting, Function<Long, Instant> dateTransformation) {
        this(activity.id, formatting.dateTimeRange(dateTransformation.apply(activity.startDate),
            dateTransformation.apply(activity.endDate)), activity.name);
    }

    @Override
    public boolean equals(Object object) {
        boolean equal;
        if (object == this) {
            equal = true;
        } else if (object != null && object.getClass().isAssignableFrom(FoundActivity.class)) {
            FoundActivity other = (FoundActivity) object;
            equal = id == other.id && date.equals(other.date) && name.equals(other.name);
        } else {
            equal = false;
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, name);
    }
}
