package com.iprogrammerr.time.ruler.model.date;

import com.iprogrammerr.time.ruler.mock.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LimitedDateTest {

    @Test
    public void parsesDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LimitedDate limitedDate = new LimitedDate(new DateParsing(formatter));
        LocalDate now = LocalDate.now(Clock.systemUTC());
        String date = now.format(formatter);
        MatcherAssert.assertThat("Does not return the same date", now.atStartOfDay().toInstant(ZoneOffset.UTC),
            Matchers.equalTo(limitedDate.fromString(date)));
    }

    @Test
    public void returnsDefaultDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LimitedDate limitedDate = new LimitedDate(new DateParsing(formatter));
        Instant now = Instant.now();
        String date = new RandomStrings().alphabetic();
        MatcherAssert.assertThat("Does not return default date", now,
            Matchers.equalTo(limitedDate.fromString(date, now)));
    }
}
