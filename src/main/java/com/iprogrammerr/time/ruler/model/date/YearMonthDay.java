package com.iprogrammerr.time.ruler.model.date;

import com.iprogrammerr.time.ruler.model.TypedMap;

import java.util.List;
import java.util.Map;

public class YearMonthDay {

    private static final int MAX_MONTH = 12;
    private static final int MAX_DAY = 31;
    public final int maxYear;
    private final TypedMap queryParams;
    private final String yearKey;
    private final String monthKey;
    private final String dayKey;

    public YearMonthDay(TypedMap queryParams, int maxYear, String yearKey, String monthKey, String dayKey) {
        this.queryParams = queryParams;
        this.maxYear = maxYear;
        this.yearKey = yearKey;
        this.monthKey = monthKey;
        this.dayKey = dayKey;
    }

    public YearMonthDay(TypedMap queryParams, int maxYear) {
        this(queryParams, maxYear, "year", "month", "day");
    }

    public YearMonthDay(Map<String, List<String>> queryParams, int maxYear) {
        this(new TypedMap(queryParams), maxYear);
    }

    public int year(int defaultYear) {
        if (isYearInvalid(defaultYear)) {
            throw new RuntimeException(
                String.format("%d is not a proper year value. It has to be in %d - %d range.", defaultYear, 0, maxYear)
            );
        }
        int year = queryParams.integer(yearKey, defaultYear);
        if (isYearInvalid(year)) {
            year = defaultYear;
        }
        return year;
    }

    private boolean isYearInvalid(int year) {
        return year < 0 || year > maxYear;
    }

    public int month(int defaultMonth) {
        if (isMonthInvalid(defaultMonth)) {
            throw new RuntimeException(
                String.format("%d is not a proper month value. It has to be in %d - %d range.", defaultMonth, 1, MAX_MONTH)
            );
        }
        int month = queryParams.integer(monthKey, defaultMonth);
        if (isMonthInvalid(month)) {
            month = defaultMonth;
        }
        return month;
    }

    private boolean isMonthInvalid(int month) {
        return month < 1 || month > MAX_MONTH;
    }

    public int day(int defaultDay) {
        if (isDayInvalid(defaultDay)) {
            throw new RuntimeException(
                String.format("%d is not a proper day value. It has to be in %d - %d range.", defaultDay, 1, MAX_DAY)
            );
        }
        int day = queryParams.integer(dayKey, defaultDay);
        if (day < 1 || day > MAX_DAY) {
            day = defaultDay;
        }
        return day;
    }

    private boolean isDayInvalid(int day) {
        return day < 1 || day > MAX_DAY;
    }
}
