package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.HtmlResponseRedirection;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

public class SigningInRespondent {

    public static final String SIGN_IN = "sign-in";
    private static final String FAREWELL_PARAM = "farewell";
    private static final String NEW_PASSWORD_PARAM = "newPassword";
    private static final String FORM_EMAIL_NAME = "emailName";
    private static final String EMAIL_NAME_PARAM = FORM_EMAIL_NAME;
    private static final String NON_EXISTENT_USER_PARAM = "nonExistentUser";
    private static final String INACTIVE_ACCOUNT_PARAM = "inactiveAccount";
    private static final String INVALID_PASSWORD_PARAM = "invalidPassword";
    private static final String NOT_USER_PASSWORD_PARAM = "notUserPassword";
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

    public HtmlResponseRedirection signInPage(String activation, String emailName, boolean farewell,
        boolean newPassword, boolean nonExistentUser, boolean inactiveAccount, boolean notUserPassword,
        boolean invalidPassword) {
        HtmlResponseRedirection response;
        if (activation.isEmpty()) {
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
            response = new HtmlResponseRedirection(view);
        } else {
            //TODO refactor
            response = new HtmlResponseRedirection(new HtmlResponse(""), activate(activation));
        }
        return response;
    }

    public Redirection signIn(HttpServletRequest request, String emailName, String password) {
        ValidateableEmail email = new ValidateableEmail(emailName);
        ValidateableName name = new ValidateableName(emailName);
        ValidateablePassword validateablePassword = new ValidateablePassword(password);
        Redirection redirection;
        if ((email.isValid() || name.isValid()) && validateablePassword.isValid()) {
            redirection = signInOrSetError(request, email.isValid() ? email.value() : name.value(),
                hashing.hash(password));
        } else if (!validateablePassword.isValid()) {
            redirection = redirect(emailName, INVALID_PASSWORD_PARAM, true);
        } else {
            redirection = redirect(EMAIL_NAME_PARAM, emailName);
        }
        return redirection;
    }

    private Redirection redirect(String key, Object param) {
        return new Redirection(new UrlQueryBuilder().put(key, param).build("/" + SIGN_IN));
    }

    private Redirection redirect(String emailName, String key, Object param) {
        Redirection redirection;
        if (emailName.isEmpty()) {
            redirection = redirect(key, param);
        } else {
            redirection = new Redirection(new UrlQueryBuilder().put(EMAIL_NAME_PARAM, emailName)
                .put(key, param).build("/" + SIGN_IN));
        }
        return redirection;
    }

    private Redirection signInOrSetError(HttpServletRequest request, String emailOrName, String passwordHash) {
        Redirection redirection;
        Optional<User> user = withEmailOrName(emailOrName);
        if (user.isPresent()) {
            User userVal = user.get();
            if (userVal.active && passwordHash.equals(userVal.password)) {
                identity.create(userVal.id, request);
                //TODO proper redirect;
                redirection = new Redirection("??");
            } else if (!userVal.active) {
                redirection = redirect(emailOrName, INACTIVE_ACCOUNT_PARAM, true);
            } else {
                redirection = redirect(emailOrName, NOT_USER_PASSWORD_PARAM, true);
            }
        } else {
            redirection = redirect(emailOrName, NON_EXISTENT_USER_PARAM, true);
        }
        return redirection;
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

    private String activate(String activation) {
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
            //TODO proper redirection;
            return "";
        }
        throw new ResponseException(ErrorCode.INVALID_ACTIVATION_LINK);
    }

    private String userHash(String email, String name, long id) {
        return hashing.hash(email, name, String.valueOf(id));
    }

    public Redirection redirectWithFarewell() {
        return redirect(FAREWELL_PARAM, true);
    }

    public Redirection redirectWithNewPassword() {
        return redirect(NEW_PASSWORD_PARAM, true);
    }
}
