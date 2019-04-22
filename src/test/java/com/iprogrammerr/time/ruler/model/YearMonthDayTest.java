package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.model.date.YearMonthDay;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class YearMonthDayTest {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final int RANDOM_BOUND = 100;
    private static final int MIN_INVALID_MONTH = 13;
    private static final int MIN_INVALID_DAY = 32;
    private final Random random = new Random();

    @Test
    public void returnsProperValues() {
        int maxYear = ZonedDateTime.now().getYear() + random.nextInt(RANDOM_BOUND);
        Integer year = randomYear(maxYear);
        Integer month = randomMonth();
        Integer day = randomDay();

        Map<String, List<String>> source = new HashMap<>();
        source.put(YEAR, Collections.singletonList(year.toString()));
        source.put(MONTH, Collections.singletonList(month.toString()));
        source.put(DAY, Collections.singletonList(day.toString()));
        YearMonthDay yearMonthDay = new YearMonthDay(source, maxYear);

        MatcherAssert.assertThat("Does not return proper values", Arrays.asList(year, month, day),
            Matchers.contains(yearMonthDay.year(year), yearMonthDay.month(month), yearMonthDay.day(day)));
    }

    private int randomYear(int bound) {
        return random.nextInt(bound);
    }

    private int randomYear() {
        return randomYear(Integer.MAX_VALUE);
    }

    private int randomMonth() {
        return 1 + random.nextInt(MIN_INVALID_MONTH - 1);
    }

    private int randomDay() {
        return 1 + random.nextInt(MIN_INVALID_DAY - 1);
    }

    @Test
    public void returnsDefaultValuesIfEmpty() {
        int maxYear = ZonedDateTime.now().getYear() + random.nextInt(RANDOM_BOUND);
        int year = randomYear(maxYear);
        int month = randomMonth();
        int day = randomDay();
        YearMonthDay yearMonthDay = new YearMonthDay(new HashMap<>(), maxYear);
        MatcherAssert.assertThat("Does not return default values", Arrays.asList(year, month, day),
            Matchers.contains(yearMonthDay.year(year), yearMonthDay.month(month), yearMonthDay.day(day)));
    }

    @Test
    public void throwsExceptionIfDefaultYearIsInvalid() {
        int maxYear = randomYear();
        int invalidYear = inInvalidRange(maxYear + 1);
        YearMonthDay yearMonthDay = new YearMonthDay(new HashMap<>(), maxYear);
        String message = String.format(
            "%d is not a proper year value. It has to be in %d - %d range.", invalidYear, 0, maxYear
        );
        MatcherAssert.assertThat("Does not throw exception with proper message", () -> yearMonthDay.year(invalidYear),
            new ThrowsMatcher(message));
    }

    private int inInvalidRange(int value) {
        return random.nextBoolean() ? -1 : value + random.nextInt(RANDOM_BOUND);
    }

    @Test
    public void throwsExceptionIfDefaultMonthIsInvalid() {
        int invalidMonth = inInvalidRange(MIN_INVALID_MONTH);
        YearMonthDay yearMonthDay = new YearMonthDay(new HashMap<>(), random.nextInt());
        String message = String.format(
            "%d is not a proper month value. It has to be in %d - %d range.", invalidMonth, 1, MIN_INVALID_MONTH - 1
        );
        MatcherAssert.assertThat("Does not throw exception with proper message", () -> yearMonthDay.month(invalidMonth),
            new ThrowsMatcher(message));
    }

    @Test
    public void throwsExceptionIfDefaultDayIsInvalid() {
        int invalidDay = inInvalidRange(MIN_INVALID_DAY);
        YearMonthDay yearMonthDay = new YearMonthDay(new HashMap<>(), random.nextInt());
        String message = String.format(
            "%d is not a proper day value. It has to be in %d - %d range.", invalidDay, 1, MIN_INVALID_DAY - 1
        );
        MatcherAssert.assertThat("Does not throw exception with proper message", () -> yearMonthDay.day(invalidDay),
            new ThrowsMatcher(message));
    }

    @Test
    public void returnsDefaultYearIfGivenIsInvalid() {
        int maxYear = randomYear();
        int invalidDay = inInvalidRange(maxYear + 1);
        int defaultYear = randomYear(maxYear);
        Map<String, List<String>> source = new HashMap<>();
        source.put(YEAR, Collections.singletonList(String.valueOf(invalidDay)));
        YearMonthDay yearMonthDay = new YearMonthDay(source, maxYear);
        MatcherAssert.assertThat("Does not return default year value", yearMonthDay.year(defaultYear),
            Matchers.equalTo(defaultYear));
    }

    @Test
    public void returnsDefaultMonthIfGivenIsInvalid() {
        int invalidMonth = inInvalidRange(MIN_INVALID_MONTH);
        int defaultMonth = randomMonth();
        Map<String, List<String>> source = new HashMap<>();
        source.put(MONTH, Collections.singletonList(String.valueOf(invalidMonth)));
        YearMonthDay yearMonthDay = new YearMonthDay(source, randomYear());
        MatcherAssert.assertThat("Does not return default month value", yearMonthDay.month(defaultMonth),
            Matchers.equalTo(defaultMonth));
    }

    @Test
    public void returnsDefaultDayIfGivenIsInvalid() {
        int invalidDay = inInvalidRange(MIN_INVALID_DAY);
        int defaultDay = randomDay();
        Map<String, List<String>> source = new HashMap<>();
        source.put(DAY, Collections.singletonList(String.valueOf(invalidDay)));
        YearMonthDay yearMonthDay = new YearMonthDay(source, randomYear());
        MatcherAssert.assertThat("Does not return default day value", yearMonthDay.day(defaultDay),
            Matchers.equalTo(defaultDay));
    }
}
