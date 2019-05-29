package com.iprogrammerr.time.ruler.model.date;

import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateParsingTest {

    @Test
    public void parsesDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateParsing dateParsing = new DateParsing(formatter);
        LocalDate now = LocalDate.now(Clock.systemUTC());
        String date = now.format(formatter);
        MatcherAssert.assertThat("Does not return the same firstDate", now.atStartOfDay().toInstant(ZoneOffset.UTC),
            Matchers.equalTo(dateParsing.read(date, Instant.now())));

    }

    @Test
    public void returnsDefaultDate() {
        DateParsing dateParsing = new DateParsing();
        String date = new RandomStrings().alphabetic();
        Instant defaultDate = Instant.now();
        MatcherAssert.assertThat("Does not return the same firstDate", defaultDate,
            Matchers.equalTo(dateParsing.read(date, defaultDate)));
    }

    @Test
    public void writesDate() {
        ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateParsing dateParsing = new DateParsing(formatter);
        String expected = formatter.format(date);
        MatcherAssert.assertThat("Writes incorrect firstDate", expected,
            Matchers.equalTo(dateParsing.write(date.toInstant())));
    }
}
