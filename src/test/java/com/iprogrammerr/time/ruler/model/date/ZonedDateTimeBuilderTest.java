package com.iprogrammerr.time.ruler.model.date;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

public class ZonedDateTimeBuilderTest {

    private static final int MAX_YEAR = 3000;

    @Test
    public void buildsZonedDateTime() {
        Random random = new Random();
        ZonedDateTime now = ZonedDateTime.now();
        ZoneId zoneId = now.getZone();
        int year = random.nextInt(MAX_YEAR);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(now.withMonth(month).toLocalDate().lengthOfMonth());
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        int nanoSecond = random.nextInt(1_000_000_000);
        ZonedDateTime expected = ZonedDateTime.of(year, month, day, hour, minute, second, nanoSecond, zoneId);
        ZonedDateTime actual = new ZonedDateTimeBuilder(zoneId).withYear(year).withMonth(month).withDay(day)
            .withHour(hour).withMinute(minute).withSecond(second).withNano(nanoSecond)
            .build();
        MatcherAssert.assertThat("Does not build proper ZonedDateTime", actual, Matchers.equalTo(expected));
    }

    @Test
    public void buildsDefaultZonedDateTime() {
        ZonedDateTime expected = ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime actual = new ZonedDateTimeBuilder().build();
        MatcherAssert.assertThat("Does not build default ZonedDateTime", actual, Matchers.equalTo(expected));
    }
}
