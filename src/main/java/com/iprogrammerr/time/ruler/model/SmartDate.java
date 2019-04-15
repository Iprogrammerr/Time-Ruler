package com.iprogrammerr.time.ruler.model;

import java.util.Calendar;

public class SmartDate {

    private final long date;

    public SmartDate(long date) {
        this.date = date;
    }

    public long withOffset(int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTimeInMillis();
    }

    public long withOffset(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTimeInMillis();
    }

    public long dayBeginning() {
        return withOffset(0, 0, 0);
    }

    public long dayEnd() {
        return withOffset(23, 59, 59);
    }
}
