package com.iprogrammerr.time.ruler.model.date;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class SmartDate {

    private final ZonedDateTime date;

    public SmartDate(ZonedDateTime date) {
        this.date = date;
    }

    public SmartDate(long date) {
        this(ZonedDateTime.ofInstant(Instant.ofEpochSecond(date), ZoneOffset.UTC));
    }

    public SmartDate() {
        this(ZonedDateTime.now(Clock.systemUTC()));
    }

    public long ofYearMonthSeconds(int year, int month) {
        return ofYearMonth(year, month).toEpochSecond();
    }

    public ZonedDateTime ofYearMonth(int year, int month) {
        return ofYearMonthDay(year, month, date.getDayOfMonth());
    }

    public ZonedDateTime ofYearMonthDay(int year, int month, int day) {
        return ZonedDateTime.of(
            year, month, day, date.getHour(), date.getMinute(), date.getSecond(), date.getNano(), date.getZone()
        );
    }

    public long ofTime(int hour, int minute, int second) {
        return ofTimeDate(hour, minute, second).toEpochSecond();
    }

    private ZonedDateTime ofTimeDate(int hour, int minute, int second) {
        return ZonedDateTime.of(
            date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
            hour, minute, second, 0, date.getZone()
        );
    }

    public long dayBeginning() {
        return ofTime(0, 0, 0);
    }

    public long dayEnd() {
        return ofTime(23, 59, 59);
    }

    public Instant withTime(Instant time) {
        return ofTimeDate(0, 0, 0).plusSeconds(time.getEpochSecond()).toInstant();
    }
}
