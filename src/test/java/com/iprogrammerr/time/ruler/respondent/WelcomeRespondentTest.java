package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.mock.FakeViews;
import com.iprogrammerr.time.ruler.mock.JavalinContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class WelcomeRespondentTest {

    @Test
    public void returnsWelcomeView() {
        FakeViews views = new FakeViews("index.html");
        WelcomeRespondent respondent = new WelcomeRespondent(views);
        JavalinContext javalinContext = new JavalinContext();
        respondent.welcome(javalinContext.context());
        MatcherAssert.assertThat(
            "Does not return welcome view", views.view(""), Matchers.equalTo(javalinContext.read())
        );
    }
}
