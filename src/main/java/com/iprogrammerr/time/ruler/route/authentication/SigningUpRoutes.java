package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.TypedMap;
import com.iprogrammerr.time.ruler.model.form.FormParams;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningUpRespondent;
import com.iprogrammerr.time.ruler.route.Routes;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.net.HttpURLConnection;

public class SigningUpRoutes implements Routes {

    private final SigningUpRespondent respondent;

    public SigningUpRoutes(SigningUpRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        app.get(SigningUpRespondent.SIGN_UP, this::showSignUp);
        app.get(SigningUpRespondent.SIGN_UP_SUCCESS, ctx -> {
            ctx.status(HttpURLConnection.HTTP_OK);
            ctx.html(respondent.signUpSuccessPage().html);
        });
        app.post(SigningUpRespondent.SIGN_UP, this::signUp);
        app.get(SigningUpRespondent.ACTIVATION, this::activateAccount);
    }

    private void showSignUp(Context context) {
        TypedMap params = new TypedMap(context.queryParamMap());
        String email = params.stringValue(QueryParams.EMAIL);
        String name = params.stringValue(QueryParams.NAME);
        HtmlResponse response;
        if (email.isEmpty() && name.isEmpty()) {
            response = respondent.signUpPage();
        } else {
            response = respondent.invalidSignUpPage(email, name, params.booleanValue(QueryParams.EMAIL_TAKEN),
                params.booleanValue(QueryParams.NAME_TAKEN), params.booleanValue(QueryParams.INVALID_PASSWORD));
        }
        context.html(response.html);
    }

    private void signUp(Context context) {
        String email = context.formParam(FormParams.EMAIL);
        String name = context.formParam(FormParams.NAME);
        String password = context.formParam(FormParams.PASSWORD);
        context.redirect(respondent.signUp(email, name, password).location);
    }

    private void activateAccount(Context context) {
        Redirection redirection = respondent.activateAccount(context.queryParam(QueryParams.HASH, ""));
        context.redirect(redirection.location);
    }
}