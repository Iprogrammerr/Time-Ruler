package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;

import java.text.DateFormat;
import java.time.Instant;

public class ForListActivity {

    public final String name;
    public final String start;
    public final String end;

    public ForListActivity(String name, String start, String end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public ForListActivity(Activity activity, DateFormat format) {
        this(activity.name, format.format(Instant.ofEpochSecond(activity.startTime).toEpochMilli()),
            format.format(Instant.ofEpochSecond(activity.endTime).toEpochMilli()));
    }
}
