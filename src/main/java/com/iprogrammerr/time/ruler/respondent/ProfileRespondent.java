package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningOutRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.ProfileViews;
import io.javalin.Context;
import io.javalin.Javalin;

public class ProfileRespondent implements GroupedRespondent {

    private static final String PROFILE = "profile";
    private static final String PROFILE_EMAIL = PROFILE + "/email";
    private static final String PROFILE_NAME = PROFILE + "/name";
    private static final String PROFILE_PASSWORD = PROFILE + "/password";
    private static final String FORM_EMAIL = "email";
    private static final String FORM_NAME = "name";
    private static final String FORM_OLD_PASSWORD = "oldPassword";
    private static final String FORM_NEW_PASSWORD = "newPassword";
    private static final String EMAIL_PARAM = FORM_EMAIL;
    private static final String EMAIL_CHANGED_PARAM = "emailChanged";
    private static final String INVALID_EMAIL_PARAM = "invalidEmail";
    private static final String USED_EMAIL_PARAM = "usedEmail";
    private static final String NAME_PARAM = FORM_NAME;
    private static final String NAME_CHANGED_PARAM = "nameChanged";
    private static final String INVALID_NAME_PARAM = "invalidName";
    private static final String USED_NAME_PARAM = "usedName";
    private static final String INVALID_OLD_PASSWORD_PARAM = "invalidOldPassword";
    private static final String NOT_USER_PASSWORD_PARAM = "notUserPassword";
    private static final String INVALID_NEW_PASSWORD_PARAM = "invalidNewPassword";
    private final SigningOutRespondent respondent;
    private final Identity<Long> identity;
    private final Users users;
    private final UsersActualization actualization;
    private final Hashing hashing;
    private final ProfileViews views;
    private String redirect;

    public ProfileRespondent(SigningOutRespondent respondent, Identity<Long> identity, Users users,
        UsersActualization actualization, Hashing hashing, ProfileViews views) {
        this.respondent = respondent;
        this.identity = identity;
        this.users = users;
        this.actualization = actualization;
        this.hashing = hashing;
        this.views = views;
        this.redirect = "";
    }

    @Override
    public void init(String group, Javalin app) {
        String profile = group + PROFILE;
        app.get(profile, this::showProfile);
        app.post(PROFILE_EMAIL, this::updateEmail);
        app.post(PROFILE_NAME, this::updateName);
        app.post(PROFILE_PASSWORD, this::updatePassword);
        redirect = "/" + profile;
    }

    private void showProfile(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.queryParam(EMAIL_PARAM, ""));
        ValidateableName name = new ValidateableName(context.queryParam(NAME_PARAM, ""));
        boolean emailChanged = queryParam(context, EMAIL_CHANGED_PARAM);
        boolean invalidEmail = queryParam(context, INVALID_EMAIL_PARAM);
        boolean usedEmail = queryParam(context, USED_EMAIL_PARAM);
        boolean nameChanged = queryParam(context, NAME_CHANGED_PARAM);
        boolean invalidName = queryParam(context, INVALID_NAME_PARAM);
        boolean usedName = queryParam(context, USED_NAME_PARAM);
        boolean invalidOldPassword = queryParam(context, INVALID_OLD_PASSWORD_PARAM);
        boolean notUserPassword = queryParam(context, NOT_USER_PASSWORD_PARAM);
        boolean invalidNewPassword = queryParam(context, INVALID_NEW_PASSWORD_PARAM);
        User user = users.user(identity.value(context.req));
        String view;
        if ((invalidEmail && !email.isValid()) || usedEmail) {
            view = views.invalidEmailView(email, usedEmail, user.name);
        } else if ((invalidName && !name.isValid()) || usedName) {
            view = views.invalidNameView(name, usedName, user.email);
        } else if (invalidOldPassword || notUserPassword || invalidNewPassword) {
            view = views.invalidPasswordView(user, invalidOldPassword, notUserPassword, invalidNewPassword);
        } else if (emailChanged) {
            view = views.emailChangedView(user);
        } else if (nameChanged) {
            view = views.nameChangedView(user);
        } else {
            view = views.view(user);
        }
        context.html(view);
    }

    private boolean queryParam(Context context, String key) {
        return context.queryParam(key, Boolean.class, Boolean.toString(false)).get();
    }

    private void updateEmail(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.formParam(FORM_EMAIL));
        if (email.isValid() && users.withEmail(email.value()).isPresent()) {
            redirectToInvalidEmail(context, email, true);
        } else if (!email.isValid()) {
            redirectToInvalidEmail(context, email, false);
        } else {
            actualization.updateEmail(identity.value(context.req), email.value());
            redirectToEmailChanged(context);
        }
    }

    private void updateName(Context context) {
        ValidateableName name = new ValidateableName(context.formParam(FORM_NAME));
        if (name.isValid() && users.withName(name.value()).isPresent()) {
            redirectToInvalidName(context, name, true);
        } else if (!name.isValid()) {
            redirectToInvalidName(context, name, false);
        } else {
            actualization.updateName(identity.value(context.req), name.value());
            redirectToNameChanged(context);
        }
    }

    private void updatePassword(Context context) {
        ValidateablePassword oldPassword = new ValidateablePassword(context.formParam(FORM_OLD_PASSWORD));
        ValidateablePassword newPassword = new ValidateablePassword(context.formParam(FORM_NEW_PASSWORD));
        User user = users.user(identity.value(context.req));
        if (oldPassword.isValid() && !hashing.hash(oldPassword.value()).equals(user.password)) {
            redirectToInvalidPassword(context, oldPassword, true, newPassword);
        } else if (!oldPassword.isValid() || !newPassword.isValid()) {
            redirectToInvalidPassword(context, oldPassword, false, newPassword);
        } else {
            actualization.updatePassword(user.id, hashing.hash(newPassword.value()));
            context.redirect(respondent.newPasswordSignOut(context.req).location);
        }
    }

    private void redirectToEmailChanged(Context context) {
        context.redirect(new UrlQueryBuilder().put(EMAIL_CHANGED_PARAM, true).build(redirect));
    }

    private void redirectToInvalidEmail(Context context, ValidateableEmail email, boolean usedEmail) {
        String url = new UrlQueryBuilder().put(EMAIL_PARAM, email.value()).put(INVALID_EMAIL_PARAM, !email.isValid())
            .put(USED_EMAIL_PARAM, usedEmail).build(redirect);
        context.redirect(url);
    }

    private void redirectToNameChanged(Context context) {
        context.redirect(new UrlQueryBuilder().put(NAME_CHANGED_PARAM, true).build(redirect));
    }

    private void redirectToInvalidName(Context context, ValidateableName name, boolean usedName) {
        String url = new UrlQueryBuilder().put(NAME_PARAM, name.value()).put(USED_NAME_PARAM, usedName)
            .put(INVALID_NAME_PARAM, !name.isValid()).build(redirect);
        context.redirect(url);
    }

    private void redirectToInvalidPassword(Context context, ValidateablePassword oldPassword, boolean notUserPassword,
        ValidateablePassword newPassword) {
        String url = new UrlQueryBuilder().put(INVALID_OLD_PASSWORD_PARAM, !oldPassword.isValid())
            .put(NOT_USER_PASSWORD_PARAM, notUserPassword)
            .put(INVALID_NEW_PASSWORD_PARAM, !newPassword.isValid())
            .build(redirect);
        context.redirect(url);
    }
}
