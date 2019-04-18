package com.iprogrammerr.time.ruler.model;

import java.util.List;
import java.util.Map;

public class YearMonthDay {

    private final TypedMap queryParams;
    private final String yearKey;
    private final String monthKey;
    private final String dayKey;

    public YearMonthDay(TypedMap queryParams, String yearKey, String monthKey, String dayKey) {
        this.queryParams = queryParams;
        this.yearKey = yearKey;
        this.monthKey = monthKey;
        this.dayKey = dayKey;
    }

    public YearMonthDay(TypedMap queryParams) {
        this(queryParams, "year", "month", "day");
    }

    public YearMonthDay(Map<String, List<String>> queryParams) {
        this(new TypedMap(queryParams));
    }

    public int year(int defaultYear) {
        return queryParams.integer(yearKey, defaultYear);
    }

    public int month(int defaultMonth) {
        return queryParams.integer(monthKey, defaultMonth);
    }

    public int day(int defaultDay) {
        return queryParams.integer(dayKey, defaultDay);
    }
}
