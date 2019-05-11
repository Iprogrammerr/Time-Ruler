package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.PasswordResetViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.Optional;

public class PasswordResetRespondent implements Respondent {

    public static final String PASSWORD_RESET = "password-reset";
    private static final String PASSWORD_RESET_FORM = "password-reset-form";
    private static final String EMAIL_PARAM = "email";
    private static final String HASH_PARAM = "hash";
    private static final String EMAIL_SENT_PARAM = "emailSent";
    private static final String PASSWORD_FORM = "password";
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

    @Override
    public void init(Javalin app) {
        app.get(PASSWORD_RESET, this::showPasswordReset);
        app.get(PASSWORD_RESET_FORM, this::showPasswordResetForm);
        app.post(PASSWORD_RESET, this::sentPasswordResetEmail);
        app.post(PASSWORD_RESET_FORM, this::resetPassword);
    }

    private void showPasswordReset(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.queryParam(EMAIL_PARAM, ""));
        boolean emailSent = context.queryParam(EMAIL_SENT_PARAM, Boolean.class, Boolean.toString(false)).get();
        if (emailSent && email.isValid()) {
            context.html(views.emailSentView(email.value()));
        } else if (shouldShowEmptyPasswordReset(email)) {
            context.html(views.sendEmailView());
        } else {
            context.html(views.invalidEmailView(email));
        }
    }

    private boolean shouldShowEmptyPasswordReset(ValidateableEmail email) {
        return email.value().isEmpty() || (email.isValid() && users.withEmail(email.value()).isPresent());
    }

    private void sentPasswordResetEmail(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.formParam(EMAIL_PARAM, ""));
        Optional<User> user = users.withEmail(email.value());
        if (email.isValid() && user.isPresent()) {
            if (user.get().active) {
                emails.sendPasswordResetEmail(user.get().email, passwordResetLink(user.get()));
                redirect(context, email, true);
            } else {
                context.html(views.inactiveAccountView(email.value()));
            }
        } else {
            redirect(context, email, false);
        }
    }

    private String passwordResetLink(User user) {
        return new UrlQueryBuilder().put(EMAIL_PARAM, user.email).put(HASH_PARAM, passwordResetHash(user))
            .build(PASSWORD_RESET_FORM);
    }

    //TODO limit validity
    private String passwordResetHash(User user) {
        return hashing.hash(String.valueOf(user.id), user.name, user.email, user.password);
    }

    private void redirect(Context context, ValidateableEmail email, boolean emailSent) {
        context.redirect(new UrlQueryBuilder().put(EMAIL_PARAM, email.value())
            .put(EMAIL_SENT_PARAM, emailSent).build());
    }

    private void showPasswordResetForm(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.queryParam(EMAIL_PARAM, ""));
        String hash = context.queryParam(HASH_PARAM, "");
        if (isPasswordResetRequestValid(email, hash)) {
            context.html(views.changePasswordView(passwordResetUrl(context), false));
        } else {
            throw new ResponseException(ErrorCode.INVALID_PASSWORD_RESET_LINK);
        }
    }

    private String passwordResetUrl(Context context) {
        return context.path() + "?" + context.queryString();
    }

    private boolean isPasswordResetRequestValid(ValidateableEmail email, String hash) {
        boolean valid = email.isValid();
        if (valid) {
            Optional<User> user = users.withEmail(email.value());
            valid = user.isPresent() && passwordResetHash(user.get()).equals(hash);
        }
        return valid;
    }

    private void resetPassword(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.queryParam(EMAIL_PARAM, ""));
        String hash = context.queryParam(HASH_PARAM, "");
        if (isPasswordResetRequestValid(email, hash)) {
            ValidateablePassword password = new ValidateablePassword(context.formParam(PASSWORD_FORM));
            if (password.isValid()) {
                actualization.updatePassword(email.value(), hashing.hash(password.value()));
                respondent.redirectWithNewPassword(context);
            } else {
                context.html(views.changePasswordView(passwordResetUrl(context), true));
            }
        } else {
            throw new ResponseException(ErrorCode.INVALID_PASSWORD_RESET_LINK);
        }
    }
}
