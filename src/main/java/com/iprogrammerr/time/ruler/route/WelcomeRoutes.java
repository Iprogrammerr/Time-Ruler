package com.iprogrammerr.time.ruler.route;

import com.iprogrammerr.time.ruler.respondent.WelcomeRespondent;
import io.javalin.Context;
import io.javalin.Javalin;

public class WelcomeRoutes implements Routes {

    private final WelcomeRespondent respondent;

    public WelcomeRoutes(WelcomeRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        app.get("/", this::showWelcome);
        app.get(WelcomeRespondent.WELCOME, this::showWelcome);
    }

    private void showWelcome(Context context) {
        context.html(respondent.welcomePage().html);
    }
}
