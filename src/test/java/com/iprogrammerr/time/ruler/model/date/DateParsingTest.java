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

public class DateParsingTest {

    @Test
    public void parsesDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateParsing dateParsing = new DateParsing(formatter);
        LocalDate now = LocalDate.now(Clock.systemUTC());
        String date = now.format(formatter);
        MatcherAssert.assertThat("Does not return the same date", now.atStartOfDay().toInstant(ZoneOffset.UTC),
            Matchers.equalTo(dateParsing.readOrDefault(date, Instant.now())));

    }

    @Test
    public void returnsDefaultDate() {
        DateParsing dateParsing = new DateParsing();
        String date = new RandomStrings().alphabetic();
        Instant defaultDate = Instant.now();
        MatcherAssert.assertThat("Does not return the same date", defaultDate,
            Matchers.equalTo(dateParsing.readOrDefault(date, defaultDate)));
    }
}
