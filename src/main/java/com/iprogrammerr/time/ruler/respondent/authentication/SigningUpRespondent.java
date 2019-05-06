package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.SigningUpViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;

public class SigningUpRespondent implements Respondent {

    public static final String SIGN_UP = "sign-up";
    private static final String SIGN_UP_SUCCESS = "sign-up-success";
    private static final String SIGN_IN = "sign-in";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_NAME = "name";
    private static final String FORM_PASSWORD = "password";
    private static final String ACTIVATION = "activation";
    private final SigningUpViews views;
    private final Users users;
    private final Hashing hashing;
    private final Emails emails;

    public SigningUpRespondent(SigningUpViews views, Users users, Hashing hashing, Emails emails) {
        this.views = views;
        this.users = users;
        this.hashing = hashing;
        this.emails = emails;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_UP, ctx -> ctx.html(views.valid()));
        app.get(SIGN_UP_SUCCESS, ctx -> {
            ctx.status(HttpURLConnection.HTTP_OK);
            ctx.html(views.success());
        });
        app.post(SIGN_UP, this::signUp);
    }

    public void signUp(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.formParam(FORM_EMAIL, ""));
        ValidateableName name = new ValidateableName(context.formParam(FORM_NAME, ""));
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD, ""));
        if (email.isValid() && name.isValid() && password.isValid()) {
            createUserIf(context, email.value(), name.value(), password.value());
        } else {
            context.html(views.invalid(email, name, password));
        }
    }

    private void createUserIf(Context context, String email, String name, String password) {
        boolean emailTaken = users.withEmail(email).isPresent();
        boolean nameTaken = users.withName(name).isPresent();
        if (emailTaken || nameTaken) {
            context.html(views.taken(email, name, emailTaken, nameTaken));
        } else {
            createUser(email, name, password);
            context.redirect(SIGN_UP_SUCCESS);
        }
    }

    private void createUser(String email, String name, String password) {
        long id = users.create(name, email, hashing.hash(password));
        emails.sendSignUpEmail(email, String.format("%s?%s=%s", SIGN_IN, ACTIVATION, userHash(email, name, id)));
    }

    private String userHash(String email, String name, long id) {
        return hashing.hash(email, name, String.valueOf(id));
    }
}
