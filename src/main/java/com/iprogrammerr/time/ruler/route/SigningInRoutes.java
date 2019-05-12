package com.iprogrammerr.time.ruler.route;

import com.iprogrammerr.time.ruler.respondent.HtmlResponseRedirection;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
import io.javalin.Context;
import io.javalin.Javalin;

public class SigningInRoutes implements Routes {

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
    private final SigningInRespondent respondent;

    public SigningInRoutes(SigningInRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        //TODO redirect if signed in
        app.get(SIGN_IN, this::renderSignIn);
        app.post(SIGN_IN, this::signIn);
    }

    private void renderSignIn(Context context) {
        String activation = context.queryParam(ACTIVATION, "");
        boolean farewell = queryParam(context, FAREWELL_PARAM);
        boolean newPassword = queryParam(context, NEW_PASSWORD_PARAM);
        boolean nonExistentUser = queryParam(context, NON_EXISTENT_USER_PARAM);
        boolean inactiveAccount = queryParam(context, INACTIVE_ACCOUNT_PARAM);
        boolean notUserPassword = queryParam(context, NOT_USER_PASSWORD_PARAM);
        boolean invalidPassword = queryParam(context, INVALID_PASSWORD_PARAM);
        String emailName = context.queryParam(EMAIL_NAME_PARAM, "");
        HtmlResponseRedirection response = respondent.signInPage(activation, emailName, farewell, newPassword,
            nonExistentUser, inactiveAccount, notUserPassword, invalidPassword);
        if (response.redirection.isEmpty()) {
            context.html(response.response.body);
        } else {
            context.redirect(response.redirection);
        }
    }


    private boolean queryParam(Context context, String key) {
        return context.queryParam(key, Boolean.class, Boolean.toString(false)).get();
    }

    private void signIn(Context context) {
        Redirection redirection = respondent.signIn(context.req,
            context.formParam(FORM_EMAIL_NAME), context.formParam(FORM_PASSWORD));
        context.redirect(redirection.location);
    }
}
