package com.iprogrammerr.time.ruler.view;

import io.javalin.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HtmlViewsTemplates implements ViewsTemplates {

    private final File root;

    public HtmlViewsTemplates(File root) {
        this.root = root;
    }

    @Override
    public void render(Context context, String name, Map<String, ?> params) {
        String path = new StringBuilder(root.getPath())
            .append(File.separator).append(name)
            .append(".html")
            .toString();
        context.render(path, params);
    }

    @Override
    public void render(Context context, String name) {
        render(context, name, new HashMap<>());
    }
}
