package com.iprogrammerr.time.ruler.model.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class DateTimeFormatting {

    private static final int MAX_HOUR = 23;
    private static final String HOUR_MINUTE_FORMAT = "%02d";
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

    public String hour(int hour) {
        return String.format(HOUR_MINUTE_FORMAT, hour);
    }

    public String minute(int minute) {
        return String.format(HOUR_MINUTE_FORMAT, minute);
    }

    public FormattedTimes times(int startHour, int startMinute, int endHour, int endMinute) {
        String startTime = time(fromHourMinute(startHour, startMinute));
        String endTime = time(fromHourMinute(endHour, endMinute));
        return new FormattedTimes(hour(startHour), minute(startMinute), startTime, hour(endHour),
            minute(endMinute), endTime);
    }

    private Instant fromHourMinute(int hour, int minute) {
        return Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(hour) + TimeUnit.MINUTES.toSeconds(minute));
    }

    public FormattedTimes times(ZonedDateTime start, ZonedDateTime end) {
        return times(start.getHour(), start.getMinute(), end.getHour(), end.getMinute());
    }

    public FormattedTimes times(ZonedDateTime time) {
        return times(time, time.getHour() < MAX_HOUR ? time.plusHours(1) : time);
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
