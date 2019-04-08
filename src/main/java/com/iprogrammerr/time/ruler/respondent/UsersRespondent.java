package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.User;
import com.iprogrammerr.time.ruler.model.Users;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.Views;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersRespondent implements Respondent {

    private static final String SIGN_UP = "sign-up";
    private static final String SIGN_UP_SUCCESS = "sign-up-success";
    private static final String SIGN_UP_FAILURE = "sign-up-failure";
    private static final String SIGN_IN = "sign-in";
    private static final String SIGN_OUT = "sign-out";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_EMAIL_LOGIN = "emailLogin";
    private static final String FORM_LOGIN = "login";
    private static final String FORM_PASSWORD = "password";
    private static final String ACTIVATION = "activation";
    private static final String INVALID_EMAIL_LOGIN_TEMPLATE = "invalidEmailLogin";
    private static final String INVALID_PASSWORD_TEMPLATE = "invalidPassword";
    private final Views views;
    private final ViewsTemplates templates;
    private final Users users;
    private final Hashing hashing;
    private final Emails emails;

    public UsersRespondent(
        Views views, ViewsTemplates templates, Users users, Hashing hashing,
        Emails emails
    ) {
        this.views = views;
        this.templates = templates;
        this.users = users;
        this.hashing = hashing;
        this.emails = emails;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_UP, ctx -> ctx.html(views.view(SIGN_UP)));
        app.get(SIGN_IN, ctx -> renderSignIn(ctx, false, false));

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

    private void createUser(String email, String name, String password) {
        long id = users.create(name, email, hashing.hash(password));
        emails.sendSignUpEmail(email,
            String.format("%s?%s=%s", SIGN_IN, ACTIVATION, urlEncodedUserHash(email, name, id)));
    }

    private String userHash(String email, String password, long id) {
        return hashing.hash(email, password, String.valueOf(id));
    }

    private String urlEncodedUserHash(String email, String password, long id) {
        try {
            return URLEncoder.encode(userHash(email, password, id), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void signIn(Context context) {
        String activation = context.pathParamMap().getOrDefault(ACTIVATION, "");
        if (activation.isEmpty()) {
            String emailOrLogin = context.formParam(FORM_EMAIL_LOGIN);
            signIn(
                context, new ValidateableEmail(emailOrLogin),
                new ValidateableName(emailOrLogin), new ValidateablePassword(context.formParam(FORM_PASSWORD))
            );
        } else {
            try {
                activate(context, URLDecoder.decode(activation, StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void signIn(Context context, ValidateableEmail email, ValidateableName name, ValidateablePassword password) {
        if (email.isValid() && password.isValid()) {
            signInByEmail(context, email.value(), hashing.hash(password.value()));
        } else if (name.isValid() && password.isValid()) {
            signInByLogin(context, name.value(), hashing.hash(password.value()));
        } else {
            renderSignIn(context, true, true);
        }
    }

    private void signInByEmail(Context context, String email, String passwordHash) {
        if (users.existsWithEmail(email)) {
            User user = users.byName(email);
            if (passwordHash.equals(user.password)) {
                //TODO create session, render dashboard!
            } else {
                renderSignIn(context, true, false);
            }
        } else {
            renderSignIn(context, true, false);
        }
    }

    private void signInByLogin(Context context, String login, String passwordHash) {
        if (users.existsWithName(login)) {
            User user = users.byName(login);
            if (passwordHash.equals(user.password)) {

            } else {
                renderSignIn(context, true, false);
            }
        } else {
            renderSignIn(context, true, false);
        }
    }

    private void renderSignIn(Context context, boolean invalidLoginEmail, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(INVALID_EMAIL_LOGIN_TEMPLATE, invalidLoginEmail);
        params.put(INVALID_PASSWORD_TEMPLATE, invalidPassword);
        templates.render(context, SIGN_IN, params);
    }

    //TODO simplify update, handle exceptions
    private void activate(Context context, String activation) {
        List<User> inactive = users.allInactive();
        boolean activated = false;
        for (User u : inactive) {
            String hash = userHash(u.email, u.password, u.id);
            if (activation.equals(hash)) {
                users.update(new User(u.id, u.name, u.email, u.password, true));
                activated = true;
                break;
            }
        }
        if (activated) {
            renderSignIn(context, false, false);
        } else {
            throw new RuntimeException("Given activation link is invalid");
        }
    }

    public void signOut(Context context) {

    }
}
