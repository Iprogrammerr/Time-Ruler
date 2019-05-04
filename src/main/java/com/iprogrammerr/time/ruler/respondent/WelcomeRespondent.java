package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

public class WelcomeRespondent implements Respondent {

    private static final String WELCOME = "index";
    private final ViewsTemplates templates;

    public WelcomeRespondent(ViewsTemplates templates) {
        this.templates = templates;
    }

    @Override
    public void init(Javalin app) {
        app.get("/", this::welcome);
    }

    public void welcome(Context context) {
        context.html(templates.rendered(WELCOME));
    }
}
