package com.iprogrammerr.time.ruler.model.date;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

//TODO simplify this tests
public class SmartDateTest {

    @Test
    public void returnsShiftedDateByTime() {
        Random random = new Random();
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        ZonedDateTime dateTime = ZonedDateTime.now();
        SmartDate date = new SmartDate(dateTime);
        dateTime = dateTime.withHour(hour).withMinute(minute).withSecond(second);
        MatcherAssert.assertThat(
            "Does not return properly shifted firstDate by time", dateTime.toEpochSecond(),
            Matchers.equalTo(date.ofTime(hour, minute, second))
        );
    }

    @Test
    public void returnsDayBeginning() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
        SmartDate date = new SmartDate(dateTime.toEpochSecond());
        dateTime = dateTime.withHour(0).withMinute(0).withSecond(0);
        MatcherAssert.assertThat(
            "Does not return day start", dateTime.toEpochSecond(),
            Matchers.equalTo(date.dayBeginning())
        );
    }

    @Test
    public void returnsDayEnd() {
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneOffset.UTC);
        SmartDate date = new SmartDate(dateTime.toEpochSecond());
        dateTime = dateTime.withHour(23).withMinute(59).withSecond(59);
        MatcherAssert.assertThat(
            "Does not return day end", dateTime.toEpochSecond(),
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
        MatcherAssert.assertThat(
            "Does not return properly shifted firstDate", dateTime.toEpochSecond(),
            Matchers.equalTo(date.ofYearMonthSeconds(year, month))
        );
    }
}
