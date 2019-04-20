package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.model.Initialization;

public class ValidateableTime implements Validateable<String> {

    private static final String HH_MM_SEPARATOR = ":";
    private static final int MAX_HOUR = 24;
    private static final int MAX_MINUTE = 60;
    private final String time;
    private final Initialization<Boolean> valid;

    public ValidateableTime(String time) {
        this.time = time == null ? "" : time;
        this.valid = new Initialization<>(() -> {
            boolean valid;
            try {
                String[] hourMinutes = this.time.split(HH_MM_SEPARATOR);
                int hour = Integer.parseInt(hourMinutes[0].trim());
                int minutes = Integer.parseInt(hourMinutes[1].trim());
                valid = (hour >= 0 && hour < MAX_HOUR) && (minutes >= 0 && minutes < MAX_MINUTE);
            } catch (Exception e) {
                valid = false;
            }
            return valid;
        });
    }

    @Override
    public String value() {
        if (isValid()) {
            return time;
        }
        throw new RuntimeException(String.format("%s is not a valid time", time));
    }

    @Override
    public boolean isValid() {
        return valid.value();
    }
}
