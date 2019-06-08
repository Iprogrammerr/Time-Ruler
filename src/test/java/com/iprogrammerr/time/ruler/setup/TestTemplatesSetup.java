package com.iprogrammerr.time.ruler.setup;

import com.iprogrammerr.time.ruler.model.Messages;
import com.iprogrammerr.time.ruler.view.HtmlViewsTemplates;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;

public class TestTemplatesSetup {

    private final String messagesResource;

    public TestTemplatesSetup(String messagesResource) {
        this.messagesResource = messagesResource;
    }

    public TestTemplatesSetup() {
        this("messages.properties");
    }

    public TemplateEngine engine() {
        File root = new File(TestTemplatesSetup.class.getResource("/template").getPath());
        TemplateEngine engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(root + File.separator);
        engine.setTemplateResolver(resolver);
        engine.setMessageResolver(messages());
        return engine;
    }

    public Messages messages() {
        Messages messages = new Messages();
        messages.init(messagesResource);
        return messages;
    }

    public ViewsTemplates templates() {
        return new HtmlViewsTemplates(engine());
    }
}
