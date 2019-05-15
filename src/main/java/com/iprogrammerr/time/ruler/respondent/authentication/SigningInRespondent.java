package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.model.param.SigningInParams;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class SigningInRespondent {

    public static final String SIGN_IN = "sign-in";
    private final DayPlanExecutionRespondent respondent;
    private final SigningInViews views;
    private final Users users;
    private final Hashing hashing;
    private final Identity<Long> identity;
    private final String signedInPrefix;

    public SigningInRespondent(DayPlanExecutionRespondent respondent, SigningInViews views,
        Users users, Hashing hashing, Identity<Long> identity, String signedInPrefix) {
        this.respondent = respondent;
        this.views = views;
        this.users = users;
        this.hashing = hashing;
        this.identity = identity;
        this.signedInPrefix = signedInPrefix;
    }

    public HtmlResponse signInPage(SigningInParams params) {
        String view;
        if (params.farewell) {
            view = views.withFarewellView();
        } else if (params.activation) {
            view = views.withActivationCongratulationsView();
        } else if (params.newPassword) {
            view = views.withNewPasswordView();
        } else if (params.emailName.isEmpty()) {
            view = views.validView();
        } else {
            view = views.invalidView(params.emailName, params.nonExistentUser, params.inactiveAccount,
                params.notUserPassword, params.invalidPassword);
        }
        return new HtmlResponse(view);
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
            redirection = redirection(emailName, QueryParams.INVALID_PASSWORD, true);
        } else {
            redirection = redirection(QueryParams.EMAIL_NAME, emailName);
        }
        return redirection;
    }

    private Redirection redirection(String key, Object param) {
        return new Redirection(new UrlQueryBuilder().put(key, param).build(SIGN_IN));
    }

    private Redirection redirection(String emailName, String key, Object param) {
        Redirection redirection;
        if (emailName.isEmpty()) {
            redirection = redirection(key, param);
        } else {
            redirection = new Redirection(
                new UrlQueryBuilder().put(QueryParams.EMAIL_NAME, emailName)
                    .put(key, param).build(SIGN_IN));
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
                redirection = respondent.signedInRedirection(signedInPrefix);
            } else if (!userVal.active) {
                redirection = redirection(emailOrName, QueryParams.INACTIVE_ACCOUNT, true);
            } else {
                redirection = redirection(emailOrName, QueryParams.NOT_USER_PASSWORD, true);
            }
        } else {
            redirection = redirection(emailOrName, QueryParams.NON_EXISTENT_USER, true);
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

    public Redirection withFarewellRedirection() {
        return redirection(QueryParams.FAREWELL, true);
    }

    public Redirection withNewPasswordRedirection() {
        return redirection(QueryParams.NEW_PASSWORD, true);
    }

    public Redirection withActivationCongratulationsRedirection() {
        return redirection(QueryParams.ACTIVATION, true);
    }

}
