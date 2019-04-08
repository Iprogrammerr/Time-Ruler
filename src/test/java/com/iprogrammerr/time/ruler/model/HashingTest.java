package com.iprogrammerr.time.ruler.model;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class HashingTest {

    @Test
    public void isDeterministic() throws Exception {
        Hashing hashing = new Hashing();
        String message = "message";
        hashesAreEqual(hashing.hash(message), hashing.hash(message));
    }

    private void hashesAreEqual(String first, String second) {
        MatcherAssert.assertThat("Hashes are not equal", first, Matchers.equalTo(second));
    }

    @Test
    public void isDeterministicWithMultipleArgs() throws Exception {
        Hashing hashing = new Hashing();
        String message = "message";
        String firstParam = "first";
        String secondParam = "second";
        hashesAreEqual(hashing.hash(message, firstParam, secondParam), hashing.hash(message, firstParam, secondParam));
    }
}
