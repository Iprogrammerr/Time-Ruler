package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;

import java.time.Instant;
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

    public FoundActivity(Activity activity, DateTimeFormatting formatting, Function<Long, Instant> timeTransformation) {
        this(activity.id, formatting.dateTime(timeTransformation.apply(activity.startDate)), activity.name);
    }
}
