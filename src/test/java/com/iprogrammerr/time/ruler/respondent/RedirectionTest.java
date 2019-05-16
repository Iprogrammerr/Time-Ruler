package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class RedirectionTest {

    @Test
    public void redirects() {
        String location = new RandomStrings().alphanumeric();
        MatcherAssert.assertThat("Does not redirect", new Redirection(location).location, Matchers.equalTo(location));
    }

    @Test
    public void redirectsWithPrefix() {
        RandomStrings strings = new RandomStrings();
        String prefix = strings.alphabetic();
        String location = strings.alphanumeric();
        MatcherAssert.assertThat("Does not redirect", new Redirection(location).prefixed(prefix),
            Matchers.equalTo("/" + prefix + location));
    }
}
