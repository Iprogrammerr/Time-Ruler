package com.iprogrammerr.time.ruler.security;

import com.iprogrammerr.time.ruler.model.Hashing;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class HashingTest {

    @Test
    public void isDeterministic() throws Exception {
        Hashing hashing = new Hashing();
        String message = "message";
        String first = hashing.hash(message);
        String second = hashing.hash(message);
        MatcherAssert.assertThat("Hashes are not equal", first, Matchers.equalTo(second));
    }
}
