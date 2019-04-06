package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Users;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.Views;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;

public class UsersRespondent implements Respondent {

    private static final String SIGN_UP = "sign-up";
    private static final String SIGN_UP_SUCCESS = "sign-up-success";
    private static final String SIGN_UP_FAILURE = "sign-up-failure";
    private static final String SIGN_IN = "sign-in";
    private static final String SIGN_OUT = "sign-out";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_LOGIN = "login";
    private static final String FORM_PASSWORD = "password";
    private final Views views;
    private final Users users;
    private final Hashing hashing;
    private final Emails emails;

    public UsersRespondent(Views views, Users users, Hashing hashing, Emails emails) {
        this.views = views;
        this.users = users;
        this.hashing = hashing;
        this.emails = emails;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_UP, ctx -> ctx.html(views.view(SIGN_UP)));
        app.get(SIGN_IN, ctx -> ctx.html(views.view(SIGN_IN)));

        app.post(SIGN_UP, this::signUp);
        app.post(SIGN_IN, this::signIn);
        app.post(SIGN_OUT, this::signOut);
    }

    public void signUp(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.formParam(FORM_EMAIL));
        ValidateableName name = new ValidateableName(context.formParam(FORM_LOGIN));
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD));
        if (email.isValid() && name.isValid() && password.isValid()) {
            createUser(email.value(), name.value(), password.value());
            context.html(SIGN_UP_SUCCESS);
            context.status(HttpURLConnection.HTTP_CREATED);
        } else {
            context.html(SIGN_UP_FAILURE);
            context.status(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    //TODO unmock
    private void createUser(String email, String name, String password) {
        //long id = users.create(name, email, hashing.hash(password));
        emails.sendSignUpEmail(email, String.format("%s?activation_id=%d", SIGN_IN, 1));
    }


    public void signIn(Context context) {

    }

    public void signOut(Context context) {

    }
}
