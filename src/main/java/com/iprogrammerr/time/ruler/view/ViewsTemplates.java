package com.iprogrammerr.time.ruler.view;

import io.javalin.Context;

import java.util.Map;

public interface ViewsTemplates {

    void render(Context context, String name, Map<String, ?> params);

    void render(Context context, String name);
}
