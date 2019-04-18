package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.mock.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TypedMapTest {

    @Test
    public void readsStringValue() {
        RandomStrings randomStrings = new RandomStrings();
        String key = randomStrings.alphabetic();
        String value = randomStrings.alphabetic();
        Map<String, List<String>> source = new HashMap<>();
        source.put(key, Collections.singletonList(value));
        TypedMap typedMap = new TypedMap(source);
        MatcherAssert.assertThat("Does not return proper string value", typedMap.string(key, key),
            Matchers.equalTo(value));
    }

    @Test
    public void returnsDefaultStringValue() {
        RandomStrings randomStrings = new RandomStrings();
        String key = randomStrings.alphabetic();
        String defaultValue = randomStrings.alphabetic();
        TypedMap typedMap = new TypedMap(new HashMap<>());
        MatcherAssert.assertThat("Does not return default string value", typedMap.string(key, defaultValue),
            Matchers.equalTo(defaultValue));
    }

    @Test
    public void readsIntValue() {
        RandomStrings randomStrings = new RandomStrings();
        String key = randomStrings.alphabetic();
        int value = new Random().nextInt();
        Map<String, List<String>> source = new HashMap<>();
        source.put(key, Collections.singletonList(String.valueOf(value)));
        TypedMap typedMap = new TypedMap(source);
        MatcherAssert.assertThat("Does not return proper int value", typedMap.integer(key, value),
            Matchers.equalTo(value));
    }

    @Test
    public void returnsDefaultIntValue() {
        RandomStrings randomStrings = new RandomStrings();
        String key = randomStrings.alphabetic();
        int defaultValue = new Random().nextInt();
        TypedMap typedMap = new TypedMap(new HashMap<>());
        MatcherAssert.assertThat("Does not return default int value", typedMap.integer(key, defaultValue),
            Matchers.equalTo(defaultValue));
    }

}
