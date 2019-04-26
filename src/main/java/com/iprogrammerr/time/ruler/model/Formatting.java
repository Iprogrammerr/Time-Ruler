package com.iprogrammerr.time.ruler.model;

import java.text.DateFormat;

public class Formatting {

    private final DateFormat timeFormat;

    public Formatting(DateFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String timeFromSeconds(long time) {
        return timeFormat.format(time * 1000);
    }
}
