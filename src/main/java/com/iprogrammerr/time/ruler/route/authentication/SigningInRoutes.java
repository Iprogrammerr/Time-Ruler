package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.FormParams;
import com.iprogrammerr.time.ruler.model.QueryParams;
import com.iprogrammerr.time.ruler.model.RequestParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponseRedirection;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
import com.iprogrammerr.time.ruler.route.Routes;
import io.javalin.Context;
import io.javalin.Javalin;

public class SigningInRoutes implements Routes {

    private final SigningInRespondent respondent;

    public SigningInRoutes(SigningInRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        //TODO redirect if signed in
        app.get(SigningInRespondent.SIGN_IN, this::renderSignIn);
        app.post(SigningInRespondent.SIGN_IN, this::signIn);
    }

    private void renderSignIn(Context context) {
        RequestParams params = new RequestParams(context);
        String activation = params.stringParam(QueryParams.ACTIVATION);
        boolean farewell = params.booleanParam(QueryParams.FAREWELL);
        boolean newPassword = params.booleanParam(QueryParams.NEW_PASSWORD);
        boolean nonExistentUser = params.booleanParam(QueryParams.NON_EXISTENT_USER);
        boolean inactiveAccount = params.booleanParam(QueryParams.INACTIVE_ACCOUNT);
        boolean notUserPassword = params.booleanParam(QueryParams.NOT_USER_PASSWORD);
        boolean invalidPassword = params.booleanParam(QueryParams.INVALID_PASSWORD);
        String emailName = params.stringParam(QueryParams.EMAIL_NAME);
        HtmlResponseRedirection response = respondent.signInPage(activation, emailName, farewell, newPassword,
            nonExistentUser, inactiveAccount, notUserPassword, invalidPassword);
        if (response.redirection.isEmpty()) {
            context.html(response.response.html);
        } else {
            context.redirect(response.redirection);
        }
    }

    private void signIn(Context context) {
        Redirection redirection = respondent.signIn(context.req, context.formParam(FormParams.EMAIL_NAME),
            context.formParam(FormParams.PASSWORD));
        context.redirect(redirection.location);
    }
}
