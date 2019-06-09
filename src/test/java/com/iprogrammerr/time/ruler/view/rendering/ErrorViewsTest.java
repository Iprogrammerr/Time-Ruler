package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ErrorsTranslation;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.ErrorViews;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ErrorViewsTest {

    private final TestTemplatesSetup setup = new TestTemplatesSetup();

    @Test
    public void returnsView() {
        ViewsTemplates templates = setup.templates();
        ErrorsTranslation translation = setup.messages();
        Locale locale = Locale.getDefault();
        ErrorCode code = ErrorCode.values()[new Random().nextInt(ErrorCode.values().length)];
        ErrorViews views = new ErrorViews(templates, translation);
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.ERROR, translation.translated(code, locale));
        MatcherAssert.assertThat("Does not return proper view", views.view(code, locale),
            Matchers.equalTo(templates.rendered(views.name, params)));
    }
}
