package com.iprogrammerr.time.ruler.model.date;

import com.iprogrammerr.time.ruler.model.TypedMap;

import java.util.List;
import java.util.Map;

public class YearMonth {

    private static final int MAX_MONTH = 12;
    public final int maxYear;
    private final TypedMap queryParams;
    private final String yearKey;
    private final String monthKey;

    public YearMonth(TypedMap queryParams, int maxYear, String yearKey, String monthKey) {
        this.queryParams = queryParams;
        this.maxYear = maxYear;
        this.yearKey = yearKey;
        this.monthKey = monthKey;
    }

    public YearMonth(TypedMap queryParams, int maxYear) {
        this(queryParams, maxYear, "year", "month");
    }

    public YearMonth(Map<String, List<String>> queryParams, int maxYear) {
        this(new TypedMap(queryParams), maxYear);
    }

    public int year(int defaultYear) {
        if (isYearInvalid(defaultYear)) {
            throw new RuntimeException(
                String.format("%d is not a proper year value. It has to be in %d - %d range.", defaultYear, 0, maxYear)
            );
        }
        int year = queryParams.integerValue(yearKey, defaultYear);
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
                String.format("%d is not a proper month value. It has to be in %d - %d range.", defaultMonth, 1,
                    MAX_MONTH)
            );
        }
        int month = queryParams.integerValue(monthKey, defaultMonth);
        if (isMonthInvalid(month)) {
            month = defaultMonth;
        }
        return month;
    }

    private boolean isMonthInvalid(int month) {
        return month < 1 || month > MAX_MONTH;
    }
}
