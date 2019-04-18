package com.iprogrammerr.time.ruler.model;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class YearMonthTest {

    @Test
    public void returnsProperValues() {
        Random random = new Random();
        Integer year = random.nextInt();
        Integer month = random.nextInt();
        Integer day = random.nextInt();

        Map<String, List<String>> source = new HashMap<>();
        source.put("year", Collections.singletonList(year.toString()));
        source.put("month", Collections.singletonList(month.toString()));
        source.put("day", Collections.singletonList(day.toString()));
        YearMonthDay yearMonthDay = new YearMonthDay(source);

        MatcherAssert.assertThat("Does not return proper values", Arrays.asList(year, month, day),
            Matchers.contains(yearMonthDay.year(year), yearMonthDay.month(month), yearMonthDay.day(day)));
    }

    @Test
    public void returnsDefaultValues() {
        Random random = new Random();
        int year = random.nextInt();
        int month = random.nextInt();
        int day = random.nextInt();
        YearMonthDay yearMonthDay = new YearMonthDay(new HashMap<>());
        MatcherAssert.assertThat("Does not return default values", Arrays.asList(year, month, day),
            Matchers.contains(yearMonthDay.year(year), yearMonthDay.month(month), yearMonthDay.day(day)));
    }
}
