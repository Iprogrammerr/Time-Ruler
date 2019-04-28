package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.TimeZone;

public class ExperimentalTest {

    @Test
    public void experimentalTest() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        ValidateableTime time = new ValidateableTime("00:00");
        Instant instant = time.value().plusSeconds(-7200);
        System.out.println(dateFormat.format(instant.toEpochMilli()));
    }
}
