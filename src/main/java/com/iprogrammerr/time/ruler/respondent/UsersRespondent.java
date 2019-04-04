package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.email.EmailServer;
import com.iprogrammerr.time.ruler.model.Users;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Context;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;

public class UsersRespondent implements Respondent {

    private static final String PREFIX = "user";
    private static final String SIGN_UP = "sign-up";
    private static final String SIGN_IN = "sign-in";
    private static final String SIGN_OUT = "sign-out";
    private final Views views;
    private final Users users;
    private final Hashing hashing;
    private final EmailServer server;

    public UsersRespondent(Views views, Users users, Hashing hashing, EmailServer server) {
        this.views = views;
        this.users = users;
        this.hashing = hashing;
        this.server = server;
    }

    @Override
    public void init(Javalin app) {
        app.routes(() -> {
            ApiBuilder.path(PREFIX, () -> {
                ApiBuilder.post(SIGN_UP, this::signUp);
                ApiBuilder.post(SIGN_IN, this::signIn);
                ApiBuilder.post(SIGN_OUT, this::signOut);
            });
        });
        app.get(SIGN_UP, ctx -> ctx.html(views.view(SIGN_UP)));
        app.get(SIGN_IN, ctx -> ctx.html(views.view(SIGN_IN)));
    }

    public void signUp(Context context) {

    }

    public void signIn(Context context) {

    }

    public void signOut(Context context) {

    }
}
