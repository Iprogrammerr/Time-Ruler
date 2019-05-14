package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.email.Emails;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.SigningUpViews;

public class SigningUpRespondent {

    public static final String SIGN_UP = "sign-up";
    public static final String SIGN_UP_SUCCESS = "sign-up-success";
    private final SigningUpViews views;
    private final Users users;
    private final Hashing hashing;
    private final Emails emails;

    public SigningUpRespondent(SigningUpViews views, Users users, Hashing hashing, Emails emails) {
        this.views = views;
        this.users = users;
        this.hashing = hashing;
        this.emails = emails;
    }

    public HtmlResponse signUpPage() {
        return new HtmlResponse(views.valid());
    }

    public HtmlResponse invalidSignUpPage(String email, String name, boolean emailTaken, boolean nameTaken,
        boolean invalidPassword) {
        String view;
        if (emailTaken || nameTaken) {
            view = views.taken(email, name, emailTaken, nameTaken);
        } else {
            view = views.invalid(new ValidateableEmail(email), new ValidateableName(name), invalidPassword);
        }
        return new HtmlResponse(view);
    }

    public HtmlResponse signUpSuccessPage() {
        return new HtmlResponse(views.success());
    }

    public Redirection signUp(String email, String name, String password) {
        Redirection redirection;
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        ValidateableName validateableName = new ValidateableName(name);
        ValidateablePassword validateablePassword = new ValidateablePassword(password);
        if (validateableEmail.isValid() && validateableName.isValid() && validateablePassword.isValid()) {
            redirection = createUserIf(email, name, password);
        } else {
            redirection = invalidRedirection(email, name, validateablePassword);
        }
        return redirection;
    }

    private Redirection invalidRedirection(String email, String name, ValidateablePassword password) {
        String url = new UrlQueryBuilder().put(QueryParams.EMAIL, email).put(QueryParams.NAME, name)
            .put(QueryParams.INVALID_PASSWORD, !password.isValid()).build(SIGN_UP);
        return new Redirection(url);
    }

    private Redirection takenRedirection(String email, String name, boolean emailTaken, boolean nameTaken) {
        String url = new UrlQueryBuilder().put(QueryParams.EMAIL, email).put(QueryParams.NAME, name)
            .put(QueryParams.EMAIL_TAKEN, emailTaken).put(QueryParams.NAME_TAKEN, nameTaken).build(SIGN_UP);
        return new Redirection(url);
    }

    private Redirection createUserIf(String email, String name, String password) {
        boolean emailTaken = users.withEmail(email).isPresent();
        boolean nameTaken = users.withName(name).isPresent();
        Redirection redirection;
        if (emailTaken || nameTaken) {
            redirection = takenRedirection(email, name, emailTaken, nameTaken);
        } else {
            createUser(email, name, password);
            //TODO remove this page, redirect to sign in with proper message
            redirection = new Redirection(SIGN_UP_SUCCESS);
        }
        return redirection;
    }

    private void createUser(String email, String name, String password) {
        long id = users.create(name, email, hashing.hash(password));
        emails.sendSignUpEmail(email, String.format("%s?%s=%s",
            SigningInRespondent.SIGN_IN, QueryParams.ACTIVATION,
            userHash(email, name, id)));
    }

    private String userHash(String email, String name, long id) {
        return hashing.hash(email, name, String.valueOf(id));
    }
}
