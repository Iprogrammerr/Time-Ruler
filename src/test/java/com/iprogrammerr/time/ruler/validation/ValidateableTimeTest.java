package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Random;

public class ValidateableTimeTest {

    private static final int MAX_HOUR_VALUE = 24;
    private static final int MAX_MINUTE_VALUE = 60;

    @Test
    public void returnsTrueWithValidTime() {
        Random random = new Random();
        int hour = random.nextInt(MAX_HOUR_VALUE);
        int minute = random.nextInt(MAX_MINUTE_VALUE);
        ValidateableTime time = new ValidateableTime(formattedTime(hour, minute));
        MatcherAssert.assertThat("Does not accept valid time", time.isValid(), Matchers.equalTo(true));
    }

    private String formattedTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    @Test
    public void returnsFalseWithBeyondLimitValues() {
        Random random = new Random();
        int hour = random.nextInt();
        int minute = random.nextInt(MAX_MINUTE_VALUE);
        if (hour < MAX_HOUR_VALUE) {
            minute += MAX_MINUTE_VALUE;
        }
        ValidateableTime time = new ValidateableTime(formattedTime(hour, minute));
        MatcherAssert.assertThat("Accepts invalid time", time.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsFalseWithoutProperSeparator() {
        Random random = new Random();
        int hour = random.nextInt(MAX_HOUR_VALUE);
        int minute = random.nextInt(MAX_MINUTE_VALUE);
        ValidateableTime time = new ValidateableTime(String.format("%d%d", hour, minute));
        MatcherAssert.assertThat("Accepts time without proper separator", time.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsFalseWithAlphabeticInput() {
        ValidateableTime time = new ValidateableTime(new RandomStrings().alphabetic());
        MatcherAssert.assertThat("Accepts alphabetic string as input", time.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsUnchangedIfValid() {
        Random random = new Random();
        int hour = random.nextInt(MAX_HOUR_VALUE);
        int minute = random.nextInt(MAX_MINUTE_VALUE);
        String time = formattedTime(hour, minute);
        ValidateableTime validateableTime = new ValidateableTime(time);
        MatcherAssert.assertThat("Does not returns unchanged time", validateableTime.value(), Matchers.equalTo(time));
    }

    @Test
    public void throwsExceptionWhenGettingValueOfNotValid() {
        String time = new RandomStrings().alphabetic();
        MatcherAssert.assertThat("Does not throw exception with proper message", new ValidateableTime(time)::value,
            new ThrowsMatcher(String.format("%s is not a valid time", time)));
    }
}
