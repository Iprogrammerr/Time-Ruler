package com.iprogrammerr.time.ruler.view;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HtmlViewsTemplates implements ViewsTemplates {

    private final File root;
    private final ITemplateEngine engine;

    public HtmlViewsTemplates(File root, ITemplateEngine engine) {
        this.root = root;
        this.engine = engine;
    }

    @Override
    public String rendered(String name, Map<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return engine.process(root + File.separator + name + ".html", context);
    }

    @Override
    public String rendered(String name) {
        return rendered(name, new HashMap<>());
    }
}
