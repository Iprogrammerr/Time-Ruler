package com.iprogrammerr.time.ruler.model.date;

import java.text.DateFormat;
import java.time.Instant;

public class DateTimeFormatting {

    private final DateFormat dateFormat;
    private final DateFormat timeFormat;

    public DateTimeFormatting(DateFormat dateFormat, DateFormat timeFormat) {
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    public String time(Instant time) {
        return timeFormat.format(time.toEpochMilli());
    }

    public String date(Instant date) {
        return dateFormat.format(date.toEpochMilli());
    }
}
