package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.email.EmailServer;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Users;
import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;

public class UsersRespondent implements Respondent {

    private static final String SIGN_UP = "sign-up";
    private static final String SIGN_UP_SUCCESS = "sign-up-success";
    private static final String SIGN_IN = "sign-in";
    private static final String SIGN_OUT = "sign-out";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_LOGIN = "login";
    private static final String FORM_PASSWORD = "password";
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
        app.get(SIGN_UP, ctx -> ctx.html(views.view(SIGN_UP)));
        app.get(SIGN_IN, ctx -> ctx.html(views.view(SIGN_IN)));

        app.post(SIGN_UP, this::signUp);
        app.post(SIGN_IN, this::signIn);
        app.post(SIGN_OUT, this::signOut);
    }

    //TODO proper implementations
    public void signUp(Context context) {
        String email = context.formParam(FORM_EMAIL);
        String user = context.formParam(FORM_LOGIN);
        String password = context.formParam(FORM_PASSWORD);
        String test = context.formParam("test");
        System.out.println("Test = " + test);
        context.status(HttpURLConnection.HTTP_CREATED);
        context.html(SIGN_UP_SUCCESS);
    }

    public void signIn(Context context) {

    }

    public void signOut(Context context) {

    }
}
