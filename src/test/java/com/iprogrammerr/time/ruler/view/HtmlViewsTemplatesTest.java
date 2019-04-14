package com.iprogrammerr.time.ruler.view;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import com.iprogrammerr.time.ruler.mock.JavalinContext;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HtmlViewsTemplatesTest {

    private static final int TEMPLATE_TITLE_SIZE = 10;
    private static final String TEMPLATE_TITLE = "title";
    private static final String TEMPLATE_TEXT = "text";
    private static final String TEMPLATE = "template";
    private File root = new File(HtmlViewsTemplatesTest.class.getResource("/").getPath());
    private TemplateEngine engine;

    @Before
    public void setup() {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        engine.setTemplateResolver(resolver);
        JavalinThymeleaf.configure(engine);
    }

    @Test
    public void rendersView() {
        HtmlViewsTemplates viewsTemplates = new HtmlViewsTemplates(root);
        RandomStrings randomStrings = new RandomStrings();
        Map<String, Object> params = new HashMap<>();
        params.put(TEMPLATE_TITLE, randomStrings.alphabetic(TEMPLATE_TITLE_SIZE));
        params.put(TEMPLATE_TEXT, randomStrings.alphabetic());
        org.thymeleaf.context.Context thymeleafContext = new org.thymeleaf.context.Context();
        thymeleafContext.setVariables(params);
        String rendered = engine.process(root + File.separator + TEMPLATE + ".html", thymeleafContext);
        JavalinContext javalinContext = new JavalinContext();
        viewsTemplates.render(javalinContext.context(), TEMPLATE, params);
        MatcherAssert.assertThat(
            "Rendered templates should be equal", javalinContext.read(), Matchers.equalTo(rendered)
        );
    }

    @Test
    public void throwsExceptionIfTemplateDoesNotExist() {
        HtmlViewsTemplates viewsTemplates = new HtmlViewsTemplates(root);
        String nonExistent = new RandomStrings().alphabetic(3);
        String path = root + File.separator + nonExistent + ".html";
        MatcherAssert.assertThat(
            "Should throw exception with proper message",
            () -> viewsTemplates.render(new JavalinContext().context(), nonExistent, new HashMap<>()),
            new ThrowsMatcher(path)
        );
    }
}
