package com.iprogrammerr.time.ruler.route;

import com.iprogrammerr.time.ruler.model.FormKey;
import com.iprogrammerr.time.ruler.model.QueryParamKey;
import com.iprogrammerr.time.ruler.model.RequestParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponseRedirection;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
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
        String activation = params.stringParam(QueryParamKey.ACTIVATION);
        boolean farewell = params.booleanParam(QueryParamKey.FAREWELL);
        boolean newPassword = params.booleanParam(QueryParamKey.NEW_PASSWORD);
        boolean nonExistentUser = params.booleanParam(QueryParamKey.NON_EXISTENT_USER);
        boolean inactiveAccount = params.booleanParam(QueryParamKey.INACTIVE_ACCOUNT);
        boolean notUserPassword = params.booleanParam(QueryParamKey.NOT_USER_PASSWORD);
        boolean invalidPassword = params.booleanParam(QueryParamKey.INVALID_PASSWORD);
        String emailName = params.stringParam(QueryParamKey.EMAIL_NAME);
        HtmlResponseRedirection response = respondent.signInPage(activation, emailName, farewell, newPassword,
            nonExistentUser, inactiveAccount, notUserPassword, invalidPassword);
        if (response.redirection.isEmpty()) {
            context.html(response.response.body);
        } else {
            context.redirect(response.redirection);
        }
    }

    private void signIn(Context context) {
        Redirection redirection = respondent.signIn(context.req, context.formParam(FormKey.EMAIL_NAME.value),
            context.formParam(FormKey.PASSWORD.value));
        context.redirect(redirection.location);
    }
}
