package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.form.FormParams;
import com.iprogrammerr.time.ruler.model.param.SigningInParams;
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
        SigningInParams params = SigningInParams.fromQuery(context.queryParamMap());
        context.html(respondent.signInPage(params).html);
    }

    private void signIn(Context context) {
        Redirection redirection = respondent.signIn(context.req, context.formParam(FormParams.EMAIL_NAME),
            context.formParam(FormParams.PASSWORD));
        context.redirect(redirection.location);
    }
}
