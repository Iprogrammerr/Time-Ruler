package com.iprogrammerr.time.ruler.model.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class DateTimeFormatting {

    private final DateFormat dateFormat;
    private final DateFormat timeFormat;
    private final DateFormat dateTimeFormat;

    public DateTimeFormatting(DateFormat dateFormat, DateFormat timeFormat, DateFormat dateTimeFormat) {
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.dateTimeFormat = dateTimeFormat;
    }

    public DateTimeFormatting() {
        this(new SimpleDateFormat("dd.MM.yyyy"), new SimpleDateFormat("HH:mm"),
            new SimpleDateFormat("dd.MM.yyyy, HH:mm"));
    }

    public String time(Instant time) {
        return timeFormat.format(time.toEpochMilli());
    }

    public String date(Instant date) {
        return dateFormat.format(date.toEpochMilli());
    }

    public String dateTime(Instant date) {
        return dateTimeFormat.format(date.toEpochMilli());
    }

    public String dateTimeRange(Instant date, Instant endTime) {
        return String.format("%s - %s", dateTime(date), time(endTime));
    }
}
