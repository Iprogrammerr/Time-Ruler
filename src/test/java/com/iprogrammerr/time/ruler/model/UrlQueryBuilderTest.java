package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.tool.UrlQueryBuilderThatBuildsProperQuery;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UrlQueryBuilderTest {

    private static final int MIN_KEYS_SIZE = 2;
    private static final int MAX_KEYS_SIZE = 50;
    private static final int MAX_VALUE_SIZE = 25;
    private static final double PUT_STRING_THRESHOLD = 0.3;

    @Test
    public void returnsEmpty() {
        MatcherAssert.assertThat("Does not return empty query", new UrlQueryBuilder().build(),
            Matchers.emptyString());
    }

    @Test
    public void buildsQueryWithBase() {
        buildsQuery(new RandomStrings().name());
    }

    private void buildsQuery(String base) {
        Random random = new Random();
        RandomStrings randomStrings = new RandomStrings(random);
        Map<String, Object> params = new HashMap<>();
        int keysSize = MIN_KEYS_SIZE + random.nextInt(MAX_KEYS_SIZE - 1);
        UrlQueryBuilder builder = new UrlQueryBuilder();
        for (int i = 0; i < keysSize; i++) {
            String key = randomStrings.name();
            Object value;
            if (random.nextDouble() >= PUT_STRING_THRESHOLD) {
                value = randomStrings.limitedAlphanumeric(MAX_VALUE_SIZE);
            } else {
                value = random.nextBoolean() ? random.nextBoolean() : random.nextInt();
            }
            params.put(key, value);
            builder.put(key, value);
        }
        MatcherAssert.assertThat("Does not build proper query", builder,
            new UrlQueryBuilderThatBuildsProperQuery(params, base));
    }

    @Test
    public void buildsQueryWithEmptyBase() {
        buildsQuery("");
    }
}
