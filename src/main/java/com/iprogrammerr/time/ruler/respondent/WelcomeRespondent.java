package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Context;
import io.javalin.Javalin;

import javax.servlet.http.HttpSession;

public class WelcomeRespondent implements Respondent {

    private static final String SIGNED_IN_COOKIE = "signedIn";
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
        HttpSession session = context.req.getSession(true);
        context.cookie(SIGNED_IN_COOKIE, session == null ? Boolean.toString(false) : Boolean.toString(true));
        context.html(views.view(WELCOME));
    }
}
