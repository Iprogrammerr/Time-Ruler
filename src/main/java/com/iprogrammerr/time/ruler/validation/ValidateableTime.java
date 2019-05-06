package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.model.Initialization;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ValidateableTime implements Validateable<Instant> {

    private static final String HH_MM_SEPARATOR = ":";
    private static final int MAX_HOUR = 24;
    private static final int MAX_MINUTE = 60;
    private final String time;
    private final Initialization<Instant> mappedTime;
    private final Initialization<Boolean> valid;

    public ValidateableTime(String time) {
        this.time = time;
        this.mappedTime = new Initialization<>(() -> {
            String[] hourMinutes = this.time.split(HH_MM_SEPARATOR);
            int hour = Integer.parseInt(hourMinutes[0].trim());
            if (hour < 0 || hour >= MAX_HOUR) {
                throw new RuntimeException(String.format("%d is not a validView hour value", hour));
            }
            int minutes = Integer.parseInt(hourMinutes[1].trim());
            if (minutes < 0 || minutes >= MAX_MINUTE) {
                throw new RuntimeException(String.format("%d is not a validView minutes value", minutes));
            }
            return Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(hour) + TimeUnit.MINUTES.toSeconds(minutes));
        });
        this.valid = new Initialization<>(() -> {
            boolean valid;
            try {
                mappedTime.value();
                valid = true;
            } catch (Exception e) {
                valid = false;
            }
            return valid;
        });
    }


    @Override
    public Instant value() {
        if (isValid()) {
            return mappedTime.value();
        }
        throw new RuntimeException(String.format("%s is not a validView time", time));
    }

    @Override
    public boolean isValid() {
        return valid.value();
    }
}
