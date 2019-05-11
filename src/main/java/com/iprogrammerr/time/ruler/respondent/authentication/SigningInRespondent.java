package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.List;
import java.util.Optional;

//TODO modify all post requests to follow Post/Redirect/Get pattern
// Do not allow inactive users to sign in
public class SigningInRespondent implements Respondent {

    public static final String SIGN_IN = "sign-in";
    private static final String FAREWELL_PARAM = "farewell";
    private static final String NEW_PASSWORD_PARAM = "newPassword";
    private static final String FORM_EMAIL_NAME = "emailName";
    private static final String FORM_PASSWORD = "password";
    private static final String ACTIVATION = "activation";
    private final DayPlanExecutionRespondent respondent;
    private final SigningInViews views;
    private final Users users;
    private final UsersActualization actualization;
    private final Hashing hashing;
    private final Identity<Long> identity;

    public SigningInRespondent(DayPlanExecutionRespondent respondent, SigningInViews views, Users users,
        UsersActualization actualization, Hashing hashing, Identity<Long> identity) {
        this.respondent = respondent;
        this.views = views;
        this.users = users;
        this.actualization = actualization;
        this.hashing = hashing;
        this.identity = identity;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_IN, this::renderSignIn);
        app.post(SIGN_IN, this::signIn);
    }

    private void renderSignIn(Context context) {
        String activation = context.queryParam(ACTIVATION, "");
        if (activation.isEmpty()) {
            if (context.req.getSession(false) == null) {
                showProperSignIn(context);
            } else {
                respondent.redirect(context);
            }
        } else {
            activate(context, activation);
        }
    }

    private void showProperSignIn(Context context) {
        boolean farewell = context.queryParam(FAREWELL_PARAM, Boolean.class, Boolean.toString(false)).get();
        boolean newPassword = context.queryParam(NEW_PASSWORD_PARAM, Boolean.class,
            Boolean.toString(false)).get();
        if (farewell) {
            context.html(views.withFarewellView());
        } else if (newPassword) {
            context.html(views.withNewPasswordView());
        } else {
            context.html(views.validView());
        }
    }

    private void signIn(Context context) {
        String emailOrLogin = context.formParam(FORM_EMAIL_NAME, "");
        ValidateableEmail email = new ValidateableEmail(emailOrLogin);
        ValidateableName name = new ValidateableName(emailOrLogin);
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD, ""));
        if ((email.isValid() || name.isValid()) && password.isValid()) {
            signInOrSetError(context, email.isValid() ? email.value() : name.value(), hashing.hash(password.value()));
        } else {
            context.html(views.invalidView(emailOrLogin, password.isValid()));
        }
    }

    private void signInOrSetError(Context context, String emailOrName, String passwordHash) {
        Optional<User> user = withEmailOrName(emailOrName);
        if (user.isPresent()) {
            User userVal = user.get();
            if (userVal.active && passwordHash.equals(userVal.password)) {
                identity.create(userVal.id, context.req);
                respondent.redirect(context);
            } else if (!userVal.active) {
                context.html(views.notActiveUserView(emailOrName));
            } else {
                context.html(views.notUserPasswordView(emailOrName));
            }
        } else {
            context.html(views.nonExistentUserView(emailOrName));
        }
    }

    private Optional<User> withEmailOrName(String emailOrName) {
        Optional<User> user;
        if (emailOrName.contains("@")) {
            user = users.withEmail(emailOrName);
        } else {
            user = users.withName(emailOrName);
        }
        return user;
    }

    private void activate(Context context, String activation) {
        List<User> inactive = users.allInactive();
        boolean activated = false;
        for (User u : inactive) {
            String hash = userHash(u.email, u.name, u.id);
            if (activation.equals(hash)) {
                actualization.activate(u.id);
                activated = true;
                break;
            }
        }
        if (activated) {
            context.html(views.withActivationCongratulationsView());
        } else {
            throw new ResponseException(ErrorCode.INVALID_ACTIVATION_LINK);
        }
    }

    private String userHash(String email, String name, long id) {
        return hashing.hash(email, name, String.valueOf(id));
    }

    public void redirectWithFarewell(Context context) {
        context.redirect(new UrlQueryBuilder().put(FAREWELL_PARAM, true).build("/" + SIGN_IN));
    }

    public void redirectWithNewPassword(Context context) {
        context.redirect(new UrlQueryBuilder().put(NEW_PASSWORD_PARAM, true).build("/" + SIGN_IN));
    }
}
