package com.iprogrammerr.time.ruler.model.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateParsing {

    private final DateTimeFormatter formatter;
    private final ZoneOffset zoneOffset;

    public DateParsing(DateTimeFormatter formatter, ZoneOffset zoneOffset) {
        this.formatter = formatter;
        this.zoneOffset = zoneOffset;
    }

    public DateParsing(DateTimeFormatter formatter) {
        this(formatter, ZoneOffset.UTC);
    }

    public DateParsing() {
        this(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public Instant read(String date, Instant defaultDate) {
        Instant read;
        try {
            read = LocalDate.parse(date, formatter).atStartOfDay().toInstant(zoneOffset);
        } catch (Exception e) {
            read = defaultDate;
        }
        return read;
    }

    public String write(Instant date) {
        return formatter.format(ZonedDateTime.ofInstant(date, zoneOffset));
    }
}
