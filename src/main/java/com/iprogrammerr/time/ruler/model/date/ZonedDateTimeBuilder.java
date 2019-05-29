package com.iprogrammerr.time.ruler.model.date;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeBuilder {

    private final ZoneId zone;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int nano;


    public ZonedDateTimeBuilder(ZoneId zone) {
        this.zone = zone;
        this.month = 1;
        this.day = 1;
    }

    public ZonedDateTimeBuilder() {
        this(ZoneOffset.UTC);
    }

    public ZonedDateTimeBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    public ZonedDateTimeBuilder withMonth(int month) {
        this.month = month;
        return this;
    }

    public ZonedDateTimeBuilder withDay(int day) {
        this.day = day;
        return this;
    }

    public ZonedDateTimeBuilder withHour(int hour) {
        this.hour = hour;
        return this;
    }

    public ZonedDateTimeBuilder withMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public ZonedDateTimeBuilder withSecond(int second) {
        this.second = second;
        return this;
    }

    public ZonedDateTimeBuilder withNano(int nano) {
        this.nano = nano;
        return this;
    }

    public ZonedDateTime build() {
        return ZonedDateTime.of(year, month, day, hour, minute, second, nano, zone);
    }
}
