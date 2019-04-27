package com.iprogrammerr.time.ruler.model.date;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LimitedDate {

    private final DateParsing dateParsing;
    private final int maxYearsOffset;

    public LimitedDate(DateParsing dateParsing, int maxYearsOffset) {
        this.dateParsing = dateParsing;
        this.maxYearsOffset = maxYearsOffset;
    }

    public LimitedDate(DateParsing dateParsing) {
        this(dateParsing, 100);
    }

    public Instant fromString(String date, Instant defaultDate) {
        Instant read = dateParsing.read(date, defaultDate);
        if (read.isAfter(LocalDateTime.now().plusYears(maxYearsOffset).toInstant(ZoneOffset.UTC))) {
            read = defaultDate;
        }
        return read;
    }

    public Instant fromString(String date) {
        return fromString(date, Instant.now());
    }
}
