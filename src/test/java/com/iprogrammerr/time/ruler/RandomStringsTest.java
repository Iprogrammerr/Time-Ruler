package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Random;
import java.util.regex.Pattern;

public class RandomStringsTest {

    private static final Pattern ALPHABETIC = Pattern.compile("[a-zA-Z]+");
    private static final Pattern ALPHANUMERIC = Pattern.compile("[a-zA-z0-9]+");

    @Test
    public void returnsRandomAlphabeticStrings() {
        RandomStrings random = new RandomStrings();
        MatcherAssert.assertThat(
            "Should be user", random.alphabetic(), Matchers.not(Matchers.equalTo(random.alphabetic()))
        );
    }

    @Test
    public void returnsRandomAlphabeticString() {
        RandomStrings random = new RandomStrings();
        MatcherAssert.assertThat(
            "Should contain only alphabetic characters", random.alphabetic(), Matchers.matchesPattern(ALPHABETIC)
        );
    }

    @Test
    public void returnsRandomAlphabeticStringWithGivenSize() {
        RandomStrings random = new RandomStrings();
        int size = 1 + new Random().nextInt(5);
        MatcherAssert.assertThat(
            String.format("Should contain only alphabetic characters and size = %d", size),
            random.alphabetic(size),
            Matchers.allOf(Matchers.matchesPattern(ALPHABETIC), Matchers.hasLength(size))
        );
    }

    @Test
    public void returnsRandomAlphanumericStrings() {
        RandomStrings random = new RandomStrings();
        MatcherAssert.assertThat(
            "Should be user", random.alphanumeric(), Matchers.not(Matchers.equalTo(random.alphanumeric()))
        );
    }

    @Test
    public void returnsRandomAlphanumericString() {
        RandomStrings random = new RandomStrings();
        MatcherAssert.assertThat(
            "Should contain only alphanumeric characters", random.alphanumeric(), Matchers.matchesPattern(ALPHANUMERIC)
        );
    }

    @Test
    public void returnsRandomAlphanumericStringWithGivenSize() {
        RandomStrings random = new RandomStrings();
        int size = 1 + new Random().nextInt(8);
        MatcherAssert.assertThat(
            String.format("Should contain only alphanumeric characters and size = %d", size),
            random.alphabetic(size),
            Matchers.allOf(Matchers.matchesPattern(ALPHANUMERIC), Matchers.hasLength(size))
        );
    }

    @Test
    public void returnsRandomEmail() {
        RandomStrings random = new RandomStrings();
        MatcherAssert.assertThat(
            "Should return proper user email",
            new ValidateableEmail(random.email()).isValid(),
            Matchers.equalTo(true)
        );
    }
}
