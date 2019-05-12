package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.QueryParams;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.PasswordResetViews;

import java.util.Optional;

public class PasswordResetRespondent {

    public static final String PASSWORD_RESET = "password-reset";
    public static final String PASSWORD_RESET_FORM = "password-reset-form";
    private final SigningInRespondent respondent;
    private final Users users;
    private final UsersActualization actualization;
    private final Emails emails;
    private final Hashing hashing;
    private final PasswordResetViews views;

    public PasswordResetRespondent(SigningInRespondent respondent, Users users, UsersActualization actualization,
        Emails emails, Hashing hashing, PasswordResetViews views) {
        this.respondent = respondent;
        this.users = users;
        this.actualization = actualization;
        this.emails = emails;
        this.hashing = hashing;
        this.views = views;
    }

    public HtmlResponse passwordResetPage(String email, boolean emailSent, boolean inactiveAccount) {
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        HtmlResponse response;
        if (emailSent && validateableEmail.isValid()) {
            response = new HtmlResponse(views.emailSentView(email));
        } else if (shouldShowEmptyPasswordReset(validateableEmail)) {
            response = new HtmlResponse(views.sendEmailView());
        } else if (inactiveAccount) {
            response = new HtmlResponse(views.inactiveAccountView(email));
        } else {
            response = new HtmlResponse(views.invalidEmailView(validateableEmail));
        }
        return response;
    }

    private boolean shouldShowEmptyPasswordReset(ValidateableEmail email) {
        return email.value().isEmpty() || (email.isValid() && users.withEmail(email.value()).isPresent());
    }

    public Redirection sentPasswordResetEmail(String email) {
        Redirection redirection;
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        Optional<User> user = users.withEmail(email);
        if (validateableEmail.isValid() && user.isPresent()) {
            if (user.get().active) {
                emails.sendPasswordResetEmail(user.get().email, passwordResetLink(user.get()));
                redirection = passwordResetRedirection(email, true, false);
            } else {
                redirection = passwordResetRedirection(email, false, true);
            }
        } else {
            redirection = passwordResetRedirection(email, false, false);
        }
        return redirection;
    }

    private String passwordResetLink(User user) {
        return new UrlQueryBuilder().put(QueryParams.EMAIL, user.email)
            .put(QueryParams.HASH, passwordResetHash(user))
            .build(PASSWORD_RESET_FORM);
    }

    //TODO limit validity
    private String passwordResetHash(User user) {
        return hashing.hash(String.valueOf(user.id), user.name, user.email, user.password);
    }

    private Redirection passwordResetRedirection(String email, boolean emailSent, boolean inactiveAccount) {
        return new Redirection(new UrlQueryBuilder().put(QueryParams.EMAIL, email)
            .put(QueryParams.EMAIL_SENT, emailSent).put(QueryParams.INACTIVE_ACCOUNT, inactiveAccount)
            .build(PASSWORD_RESET));
    }

    public HtmlResponse passwordResetForm(String email, String hash, String passwordResetUrl) {
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        if (isPasswordResetRequestValid(validateableEmail, hash)) {
            return new HtmlResponse(views.changePasswordView(passwordResetUrl, false));
        }
        throw new ResponseException(ErrorCode.INVALID_PASSWORD_RESET_LINK);
    }

    private boolean isPasswordResetRequestValid(ValidateableEmail email, String hash) {
        boolean valid = email.isValid();
        if (valid) {
            Optional<User> user = users.withEmail(email.value());
            valid = user.isPresent() && passwordResetHash(user.get()).equals(hash);
        }
        return valid;
    }

    public Redirection resetPassword(String email, String hash, String password, String passwordResetFormUrl) {
        Redirection redirection;
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        if (isPasswordResetRequestValid(validateableEmail, hash)) {
            if (new ValidateablePassword(password).isValid()) {
                actualization.updatePassword(email, hashing.hash(password));
                redirection = respondent.redirectWithNewPassword();
            } else {
                redirection = passwordResetFormRedirection(passwordResetFormUrl);
            }
        } else {
            throw new ResponseException(ErrorCode.INVALID_PASSWORD_RESET_LINK);
        }
        return redirection;
    }

    private Redirection passwordResetFormRedirection(String passwordResetUrl) {
        return new Redirection(new UrlQueryBuilder().put(QueryParams.INVALID_PASSWORD, true)
            .build(passwordResetUrl));
    }
}
