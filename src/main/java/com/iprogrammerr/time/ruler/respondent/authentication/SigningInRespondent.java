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

public class SigningInRespondent implements Respondent {

    public static final String SIGN_IN = "sign-in";
    private static final String FAREWELL_PARAM = "farewell";
    private static final String NEW_PASSWORD_PARAM = "newPassword";
    private static final String FORM_EMAIL_NAME = "emailName";
    private static final String FORM_PASSWORD = "password";
    private static final String EMAIL_NAME_PARAM = FORM_EMAIL_NAME;
    private static final String NON_EXISTENT_USER_PARAM = "nonExistentUser";
    private static final String INACTIVE_ACCOUNT_PARAM = "inactiveAccount";
    private static final String INVALID_PASSWORD_PARAM = "invalidPassword";
    private static final String NOT_USER_PASSWORD_PARAM = "notUserPassword";
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
        boolean farewell = queryParam(context, FAREWELL_PARAM);
        boolean newPassword = queryParam(context, NEW_PASSWORD_PARAM);
        boolean nonExistentUser = queryParam(context, NON_EXISTENT_USER_PARAM);
        boolean inactiveAccount = queryParam(context, INACTIVE_ACCOUNT_PARAM);
        boolean notUserPassword = queryParam(context, NOT_USER_PASSWORD_PARAM);
        boolean invalidPassword = queryParam(context, INVALID_PASSWORD_PARAM);
        String emailName = context.queryParam(EMAIL_NAME_PARAM, "");
        String view;
        if (farewell) {
            view = views.withFarewellView();
        } else if (newPassword) {
            view = views.withNewPasswordView();
        } else if (emailName.isEmpty()) {
            view = views.validView();
        } else {
            view = views.invalidView(emailName, nonExistentUser, inactiveAccount, notUserPassword, invalidPassword);
        }
        context.html(view);
    }

    private boolean queryParam(Context context, String key) {
        return context.queryParam(key, Boolean.class, Boolean.toString(false)).get();
    }

    private void signIn(Context context) {
        String emailName = context.formParam(FORM_EMAIL_NAME, "");
        ValidateableEmail email = new ValidateableEmail(emailName);
        ValidateableName name = new ValidateableName(emailName);
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD, ""));
        if ((email.isValid() || name.isValid()) && password.isValid()) {
            signInOrSetError(context, email.isValid() ? email.value() : name.value(), hashing.hash(password.value()));
        } else if (!password.isValid()) {
            redirect(context, emailName, INVALID_PASSWORD_PARAM, true);
        } else {
            redirect(context, EMAIL_NAME_PARAM, emailName);
        }
    }

    private void redirect(Context context, String key, Object param) {
        context.redirect(new UrlQueryBuilder().put(key, param).build("/" + SIGN_IN));
    }

    private void redirect(Context context, String emailName, String key, Object param) {
        if (emailName.isEmpty()) {
            redirect(context, key, param);
        } else {
            context.redirect(new UrlQueryBuilder().put(EMAIL_NAME_PARAM, emailName)
                .put(key, param).build("/" + SIGN_IN));
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
                redirect(context, emailOrName, INACTIVE_ACCOUNT_PARAM, true);
            } else {
                redirect(context, emailOrName, NOT_USER_PASSWORD_PARAM, true);
            }
        } else {
            redirect(context, emailOrName, NON_EXISTENT_USER_PARAM, true);
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

    //TODO refactor to follow POST/REDIRECT/GET
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
        redirect(context, FAREWELL_PARAM, true);
    }

    public void redirectWithNewPassword(Context context) {
        redirect(context, NEW_PASSWORD_PARAM, true);
    }
}
