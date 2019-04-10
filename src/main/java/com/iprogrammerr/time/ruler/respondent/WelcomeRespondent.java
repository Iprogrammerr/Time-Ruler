package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Context;
import io.javalin.Javalin;

public class WelcomeRespondent implements Respondent {

    private static final String WELCOME = "index";
    private final Views views;

    public WelcomeRespondent(Views views) {
        this.views = views;
    }

    @Override
    public void init(Javalin app) {
        app.get("/", this::welcome);
    }

    public void welcome(Context context) {
        context.html(views.view(WELCOME));
    }
}
