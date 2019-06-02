package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.param.ProfileParams;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningOutRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.ProfileViews;

import javax.servlet.http.HttpServletRequest;

public class ProfileRespondent {

    public static final String PROFILE = "profile";
    public static final String PROFILE_EMAIL = PROFILE + "/email";
    public static final String PROFILE_NAME = PROFILE + "/name";
    public static final String PROFILE_PASSWORD = PROFILE + "/password";
    private final SigningOutRespondent respondent;
    private final Identity<Long> identity;
    private final Users users;
    private final UsersActualization actualization;
    private final Hashing hashing;
    private final ProfileViews views;

    public ProfileRespondent(SigningOutRespondent respondent, Identity<Long> identity, Users users,
        UsersActualization actualization, Hashing hashing, ProfileViews views) {
        this.respondent = respondent;
        this.identity = identity;
        this.users = users;
        this.actualization = actualization;
        this.hashing = hashing;
        this.views = views;
    }

    public HtmlResponse profilePage(HttpServletRequest request, ProfileParams params) {
        ValidateableEmail email = new ValidateableEmail(params.email);
        ValidateableName name = new ValidateableName(params.name);
        User user = users.user(identity.value(request));
        String view;
        if ((!email.value().isEmpty() && !email.isValid()) || params.emailTaken) {
            view = views.invalidEmailView(email, params.emailTaken, user.name);
        } else if ((!name.value().isEmpty() && !name.isValid()) || params.nameTaken) {
            view = views.invalidNameView(name, params.nameTaken, user.email);
        } else if (params.invalidOldPassword || params.notUserPassword || params.invalidNewPassword) {
            view = views.invalidPasswordView(user, params.invalidOldPassword, params.notUserPassword,
                params.invalidNewPassword);
        } else if (params.emailChanged) {
            view = views.emailChangedView(user);
        } else if (params.nameChanged) {
            view = views.nameChangedView(user);
        } else {
            view = views.view(user);
        }
        return new HtmlResponse(view);
    }

    public Redirection updateEmail(HttpServletRequest request, String email) {
        Redirection redirection;
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        if (validateableEmail.isValid() && users.withEmail(email).isPresent()) {
            redirection = invalidEmailRedirection(email, true);
        } else if (!validateableEmail.isValid()) {
            redirection = invalidEmailRedirection(email, false);
        } else {
            actualization.updateEmail(identity.value(request), email);
            redirection = emailChangedRedirection();
        }
        return redirection;
    }

    public Redirection updateName(HttpServletRequest request, String name) {
        Redirection redirection;
        ValidateableName validateableName = new ValidateableName(name);
        if (validateableName.isValid() && users.withName(name).isPresent()) {
            redirection = invalidNameRedirection(name, true);
        } else if (!validateableName.isValid()) {
            redirection = invalidNameRedirection(name, false);
        } else {
            actualization.updateName(identity.value(request), name);
            redirection = nameChangedRedirection();
        }
        return redirection;
    }

    public Redirection updatePassword(HttpServletRequest request, String oldPassword,
        String newPassword) {
        Redirection redirection;
        ValidateablePassword validateableOldPassword = new ValidateablePassword(oldPassword);
        ValidateablePassword validateableNewPassword = new ValidateablePassword(newPassword);
        User user = users.user(identity.value(request));
        if (validateableOldPassword.isValid() && !hashing.hash(oldPassword).equals(user.password)) {
            redirection = invalidPasswordRedirection(validateableOldPassword, true,
                validateableNewPassword);
        } else if (!validateableOldPassword.isValid() || !validateableNewPassword.isValid()) {
            redirection = invalidPasswordRedirection(validateableOldPassword, false,
                validateableNewPassword);
        } else {
            actualization.updatePassword(user.id, hashing.hash(newPassword));
            redirection = respondent.newPasswordSignOut(request);
        }
        return redirection;
    }

    private Redirection emailChangedRedirection() {
        return new Redirection(new UrlQueryBuilder().put(QueryParams.EMAIL_CHANGED, true).build(PROFILE));
    }

    private Redirection invalidEmailRedirection(String email, boolean emailTaken) {
        String url = new UrlQueryBuilder().put(QueryParams.EMAIL, email)
            .put(QueryParams.EMAIL_TAKEN, emailTaken).build(PROFILE);
        return new Redirection(url);
    }

    private Redirection nameChangedRedirection() {
        return new Redirection(new UrlQueryBuilder().put(QueryParams.NAME_CHANGED, true).build(PROFILE));
    }

    private Redirection invalidNameRedirection(String name, boolean nameTaken) {
        String url = new UrlQueryBuilder().put(QueryParams.NAME, name)
            .put(QueryParams.NAME_TAKEN, nameTaken).build(PROFILE);
        return new Redirection(url);
    }

    private Redirection invalidPasswordRedirection(ValidateablePassword oldPassword,
        boolean notUserPassword, ValidateablePassword newPassword) {
        String url = new UrlQueryBuilder()
            .put(QueryParams.INVALID_OLD_PASSWORD, !oldPassword.isValid())
            .put(QueryParams.NOT_USER_PASSWORD, notUserPassword)
            .put(QueryParams.INVALID_NEW_PASSWORD, !newPassword.isValid())
            .build(PROFILE);
        return new Redirection(url);
    }
}
