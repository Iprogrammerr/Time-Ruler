package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.mock.JavalinContext;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class WelcomeRespondentTest {

    @Test
    public void returnsWelcomeView() {
        ViewsTemplates templates = new TestTemplatesSetup().templates();
        WelcomeRespondent respondent = new WelcomeRespondent(templates);
        JavalinContext javalinContext = new JavalinContext();
        respondent.welcome(javalinContext.context());
        MatcherAssert.assertThat(
            "Does not return welcome defaultView", templates.rendered("index"), Matchers.equalTo(javalinContext.read())
        );

    }
}
