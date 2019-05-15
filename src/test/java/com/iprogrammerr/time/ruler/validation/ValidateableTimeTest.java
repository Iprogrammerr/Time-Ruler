package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ValidateableTimeTest {

    private static final int MAX_HOUR_VALUE = 24;
    private static final int MAX_MINUTES_VALUE = 60;

    @Test
    public void returnsTrueWithValidTime() {
        Random random = new Random();
        int hour = random.nextInt(MAX_HOUR_VALUE);
        int minutes = random.nextInt(MAX_MINUTES_VALUE);
        ValidateableTime time = new ValidateableTime(formattedTime(hour, minutes));
        MatcherAssert.assertThat("Does not accept valid time", time.isValid(), Matchers.equalTo(true));
    }

    private String formattedTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    @Test
    public void returnsFalseWithBeyondLimitValues() {
        Random random = new Random();
        int hour = random.nextInt();
        int minutes = random.nextInt(MAX_MINUTES_VALUE);
        if (hour < MAX_HOUR_VALUE) {
            minutes += MAX_MINUTES_VALUE;
        }
        ValidateableTime time = new ValidateableTime(formattedTime(hour, minutes));
        MatcherAssert.assertThat("Accepts invalid time", time.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsFalseWithoutProperSeparator() {
        Random random = new Random();
        int hour = random.nextInt(MAX_HOUR_VALUE);
        int minute = random.nextInt(MAX_MINUTES_VALUE);
        ValidateableTime time = new ValidateableTime(String.format("%d%d", hour, minute));
        MatcherAssert.assertThat("Accepts time without proper separator", time.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsFalseWithAlphabeticInput() {
        ValidateableTime time = new ValidateableTime(new RandomStrings().alphabetic());
        MatcherAssert.assertThat("Accepts alphabetic stringValue as input", time.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsUnchangedIfValid() {
        Random random = new Random();
        int hour = random.nextInt(MAX_HOUR_VALUE);
        int minutes = random.nextInt(MAX_MINUTES_VALUE);
        String time = formattedTime(hour, minutes);
        ValidateableTime validateableTime = new ValidateableTime(time);
        MatcherAssert.assertThat(
            "Does not returns unchanged time", validateableTime.value(),
            Matchers.equalTo(
                Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(hour) + TimeUnit.MINUTES.toSeconds(minutes))
            )
        );
    }

    @Test
    public void throwsExceptionWhenGettingValueOfNotValid() {
        String time = new RandomStrings().alphabetic();
        MatcherAssert.assertThat("Does not throw exception with proper message", new ValidateableTime(time)::value,
            new ThrowsMatcher(String.format("%s is not a valid time", time)));
    }
}
