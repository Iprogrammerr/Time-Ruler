package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;

public class ForViewActivity {

    public final String name;
    public final String start;
    public final String end;

    public ForViewActivity(String name, String start, String end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public ForViewActivity(Activity activity, DateTimeFormatting formatting) {
        this(activity.name, formatting.timeFromSeconds(activity.startTime),
            formatting.timeFromSeconds(activity.endTime));
    }
}
