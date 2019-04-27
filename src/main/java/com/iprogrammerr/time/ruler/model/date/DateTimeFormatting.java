package com.iprogrammerr.time.ruler.model.date;

import java.text.DateFormat;

public class DateTimeFormatting {

    private final DateFormat dateFormat;
    private final DateFormat timeFormat;

    public DateTimeFormatting(DateFormat dateFormat, DateFormat timeFormat) {
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    public String timeFromSeconds(long time) {
        return timeFormat.format(time * 1000);
    }

    public String dateFromSeconds(long date) {
        return dateFormat.format(date * 1000);
    }
}
