package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class WelcomeRespondentTest {

    @Test
    public void returnsWelcomePage() {
        ViewsTemplates templates = new TestTemplatesSetup().templates();
        WelcomeRespondent respondent = new WelcomeRespondent(templates);
        MatcherAssert.assertThat("Does not return welcome defaultView", templates.rendered("index"),
            Matchers.equalTo(respondent.welcomePage().html));
    }
}
