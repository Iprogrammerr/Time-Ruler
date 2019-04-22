package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.session.UtcOffsetAttribute;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.Views;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.net.HttpURLConnection;
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
    private static final String FORM_UTC_OFFSET = "utcOffset";
    private static final String ACTIVATION = "activation";
    private static final String MESSAGE_TEMPLATE = "message";
    private static final String EMAIL_LOGIN_TEMPLATE = "emailLogin";
    private static final String INVALID_EMAIL_LOGIN_TEMPLATE = "invalidEmailLogin";
    private static final String INVALID_PASSWORD_TEMPLATE = "invalidPassword";
    private static final String ACTIVATION_CONGRATULATIONS = "Your account has been activated, now you can sign in!";
    private static final String SIGN_OUT_FAREWELL = "Always take charge of your time.";
    private final TodayRespondent respondent;
    private final Views views;
    private final ViewsTemplates templates;
    private final Users users;
    private final Hashing hashing;
    private final Emails emails;
    private final Identity<Long> identity;
    private final UtcOffsetAttribute offsetAttribute;

    public UsersRespondent(TodayRespondent respondent, Views views, ViewsTemplates templates, Users users,
        Hashing hashing, Emails emails, Identity<Long> identity, UtcOffsetAttribute offsetAttribute) {
        this.respondent = respondent;
        this.views = views;
        this.templates = templates;
        this.users = users;
        this.hashing = hashing;
        this.emails = emails;
        this.identity = identity;
        this.offsetAttribute = offsetAttribute;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_UP, ctx -> ctx.html(views.view(SIGN_UP)));
        app.get(SIGN_IN, this::renderSignIn);
        app.get(SIGN_OUT, this::signOut);
        app.post(SIGN_UP, this::signUp);
        app.post(SIGN_IN, this::signIn);
    }

    private void renderSignIn(Context context) {
        String activation = context.queryParam(ACTIVATION, "");
        if (activation.isEmpty()) {
            if (context.req.getSession(false) == null) {
                renderValidSignIn(context);
            } else {
                respondent.redirect(context);
            }
        } else {
            activate(context, activation);
        }
    }

    public void signUp(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.formParam(FORM_EMAIL, ""));
        ValidateableName name = new ValidateableName(context.formParam(FORM_LOGIN, ""));
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD, ""));
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
        emails.sendSignUpEmail(
            email, String.format("%s?%s=%s", SIGN_IN, ACTIVATION, userHash(email, name, id))
        );
    }

    private String userHash(String email, String name, long id) {
        return hashing.hash(email, name, String.valueOf(id));
    }
    
    public void signIn(Context context) {
        String emailOrLogin = context.formParam(FORM_EMAIL_LOGIN, "");
        ValidateableEmail email = new ValidateableEmail(emailOrLogin);
        ValidateableName name = new ValidateableName(emailOrLogin);
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD, ""));
        if ((email.isValid() || name.isValid()) && password.isValid()) {
            signInOrSetError(context, email.isValid() ? email.value() : name.value(), hashing.hash(password.value()));
        } else {
            renderSignIn(context, emailOrLogin, true);
        }
    }

    private void signInOrSetError(Context context, String emailOrName, String passwordHash) {
        if (users.existsWithEmailOrName(emailOrName)) {
            User user = users.byEmailOrName(emailOrName);
            if (passwordHash.equals(user.password)) {
                identity.create(user.id, context.req);
                int utcOffset = context.formParam(FORM_UTC_OFFSET, Integer.class).get();
                offsetAttribute.to(context.req.getSession(), utcOffset);
                respondent.redirect(context);
            } else {
                renderSignIn(context, emailOrName, false);
            }
        } else {
            renderSignIn(context, emailOrName, false);
        }
    }

    private void renderValidSignIn(Context context) {
        renderSignIn(context, "", false);
    }

    private void renderValidSignInWithMessage(Context context, String message) {
        Map<String, Object> params = new HashMap<>();
        params.put(MESSAGE_TEMPLATE, message);
        templates.render(context, SIGN_IN, params);
    }

    private void renderSignIn(Context context, String invalidEmailLogin, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        if (!invalidEmailLogin.isEmpty()) {
            params.put(EMAIL_LOGIN_TEMPLATE, invalidEmailLogin);
            params.put(INVALID_EMAIL_LOGIN_TEMPLATE, true);
        } else {
            params.put(INVALID_EMAIL_LOGIN_TEMPLATE, false);
        }
        params.put(INVALID_PASSWORD_TEMPLATE, invalidPassword);
        templates.render(context, SIGN_IN, params);
    }

    //TODO simplify update, handle exceptions
    private void activate(Context context, String activation) {
        List<User> inactive = users.allInactive();
        boolean activated = false;
        for (User u : inactive) {
            String hash = userHash(u.email, u.name, u.id);
            if (activation.equals(hash)) {
                users.update(new User(u.id, u.name, u.email, u.password, true));
                activated = true;
                break;
            }
        }
        if (activated) {
            renderValidSignInWithMessage(context, ACTIVATION_CONGRATULATIONS);
        } else {
            throw new RuntimeException("Given activation link is invalid");
        }
    }

    public void signOut(Context context) {
        HttpSession session = context.req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie[] cookies = context.req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                c.setMaxAge(0);
                context.cookie(c);
            }
        }
        renderValidSignInWithMessage(context, SIGN_OUT_FAREWELL);
    }
}
