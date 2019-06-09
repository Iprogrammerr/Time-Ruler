package com.iprogrammerr.time.ruler.model.date;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SmartDateTest {

    private static final int MAX_HOUR = 23;
    private static final int DAY_SECONDS = (int) TimeUnit.DAYS.toSeconds(1);

    @Test
    public void returnsShiftedDateByTime() {
        Random random = new Random();
        int hour = random.nextInt(1 + MAX_HOUR);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        ZonedDateTime dateTime = ZonedDateTime.now();
        SmartDate date = new SmartDate(dateTime);
        dateTime = dateTime.withHour(hour).withMinute(minute).withSecond(second);
        MatcherAssert.assertThat("Does not return properly shifted firstDate by time", dateTime.toEpochSecond(),
            Matchers.equalTo(date.ofTime(hour, minute, second))
        );
    }

    @Test
    public void returnsDayBeginning() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
        SmartDate date = new SmartDate(dateTime.toEpochSecond());
        dateTime = dateTime.withHour(0).withMinute(0).withSecond(0);
        MatcherAssert.assertThat("Does not return day start", dateTime.toEpochSecond(),
            Matchers.equalTo(date.dayBeginning())
        );
    }

    @Test
    public void returnsDayEnd() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
        SmartDate date = new SmartDate(dateTime.toEpochSecond());
        dateTime = dateTime.withHour(23).withMinute(59).withSecond(59);
        MatcherAssert.assertThat("Does not return day end", dateTime.toEpochSecond(),
            Matchers.equalTo(date.dayEnd())
        );
    }

    @Test
    public void returnsShiftedDate() {
        Random random = new Random();
        ZonedDateTime dateTime = ZonedDateTime.now();
        SmartDate date = new SmartDate(dateTime);
        int year = dateTime.getYear() + random.nextInt(100);
        int month = 1 + random.nextInt(12);
        dateTime = dateTime.withYear(year).withMonth(month);
        MatcherAssert.assertThat("Does not return properly shifted firstDate", dateTime.toEpochSecond(),
            Matchers.equalTo(date.ofYearMonthSeconds(year, month))
        );
    }

    @Test
    public void returnsWithTimeDate() {
        ZonedDateTime date = ZonedDateTime.now();
        Instant time = Instant.ofEpochSecond(new Random().nextInt(DAY_SECONDS));
        long expected = ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
            0, 0, 0, 0, date.getZone())
            .plusSeconds(time.getEpochSecond()).toEpochSecond();
        MatcherAssert.assertThat("Does not return with time date", expected,
            Matchers.equalTo(new SmartDate(date).withTime(time).getEpochSecond()));
    }

    @Test
    public void returnsTheSameDay() {
        ZonedDateTime date = ZonedDateTime.now();
        if (date.getHour() == MAX_HOUR) {
            date = date.withHour(0);
        }
        int offset = new Random().nextInt((int) TimeUnit.HOURS.toSeconds(MAX_HOUR - date.getHour()));
        Instant sameDay = Instant.ofEpochSecond(date.toEpochSecond()).plusSeconds(offset);
        MatcherAssert.assertThat("Does not return the same day", new SmartDate(date).isTheSameDay(sameDay),
            Matchers.equalTo(true));
    }

    @Test
    public void returnsDifferentBeforeDay() {
        returnsDifferentDay(true);
    }

    private void returnsDifferentDay(boolean before) {
        ZonedDateTime date = ZonedDateTime.now();
        int offset = DAY_SECONDS + new Random().nextInt(DAY_SECONDS);
        Instant sameDay = Instant.ofEpochSecond(date.toEpochSecond());
        if (before) {
            sameDay = sameDay.minusSeconds(offset);
        } else {
            sameDay = sameDay.plusSeconds(offset);
        }
        MatcherAssert.assertThat("Does not return different day", new SmartDate(date).isTheSameDay(sameDay),
            Matchers.equalTo(false));
    }

    @Test
    public void returnsDifferentAfterDay() {
        returnsDifferentDay(false);
    }
}
