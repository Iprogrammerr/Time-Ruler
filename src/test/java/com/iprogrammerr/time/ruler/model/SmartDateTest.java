package com.iprogrammerr.time.ruler.model;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Calendar;
import java.util.Random;

public class SmartDateTest {

    @Test
    public void returnsDateWithTimeOffset() {
        Random random = new Random();
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        Calendar calendar = Calendar.getInstance();
        SmartDate date = new SmartDate(calendar.getTimeInMillis());
        shiftCalendar(calendar, hour, minute, second);
        MatcherAssert.assertThat(
            "Does not return properly shifted date by time", calendar.getTimeInMillis(),
            Matchers.equalTo(date.withOffset(hour, minute, second))
        );
    }

    private void shiftCalendar(Calendar calendar, int hour, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
    }

    @Test
    public void returnsDayBeginning() {
        Calendar calendar = Calendar.getInstance();
        SmartDate date = new SmartDate(calendar.getTimeInMillis());
        shiftCalendar(calendar, 0, 0, 0);
        MatcherAssert.assertThat(
            "Does not return day beginning", calendar.getTimeInMillis(),
            Matchers.equalTo(date.dayBeginning())
        );
    }

    @Test
    public void returnsDayEnd() {
        Calendar calendar = Calendar.getInstance();
        SmartDate date = new SmartDate(calendar.getTimeInMillis());
        shiftCalendar(calendar, 23, 59, 59);
        MatcherAssert.assertThat(
            "Does not return day end", calendar.getTimeInMillis(),
            Matchers.equalTo(date.dayEnd())
        );
    }

    @Test
    public void returnsDateWithOffset() {
        Random random = new Random();
        int monthOffset = random.nextInt(100);
        int dayOffset = random.nextInt(31);
        Calendar calendar = Calendar.getInstance();
        SmartDate date = new SmartDate(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, monthOffset);
        calendar.set(Calendar.DAY_OF_MONTH, dayOffset);
        MatcherAssert.assertThat(
            "Does not return properly shifted date", calendar.getTimeInMillis(),
            Matchers.equalTo(date.withOffset(monthOffset, dayOffset))
        );
    }
}
