package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class HtmlResponseTest {

    @Test
    public void returnsGiven() {
        String content = "<html>" + new RandomStrings().alphanumeric() + "</html>";
        MatcherAssert.assertThat("Should return received content", new HtmlResponse(content).html,
            Matchers.equalTo(content));
    }
}
