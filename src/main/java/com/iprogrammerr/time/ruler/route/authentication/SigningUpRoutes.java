package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.FormKey;
import com.iprogrammerr.time.ruler.model.QueryParamKey;
import com.iprogrammerr.time.ruler.model.RequestParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningUpRespondent;
import com.iprogrammerr.time.ruler.route.Routes;
import io.javalin.Context;
import io.javalin.Javalin;

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
    }

    private void showSignUp(Context context) {
        RequestParams params = new RequestParams(context);
        String email = params.stringParam(QueryParamKey.EMAIL);
        String name = params.stringParam(QueryParamKey.NAME);
        HtmlResponse response;
        if (email.isEmpty() && name.isEmpty()) {
            response = respondent.signUpPage();
        } else {
            response = respondent.invalidSignUpPage(email, name, params.booleanParam(QueryParamKey.EMAIL_TAKEN),
                params.booleanParam(QueryParamKey.NAME_TAKEN), params.booleanParam(QueryParamKey.INVALID_PASSWORD));
        }
        context.html(response.html);
    }

    private void signUp(Context context) {
        String email = context.formParam(FormKey.EMAIL.value);
        String name = context.formParam(FormKey.NAME.value);
        String password = context.formParam(FormKey.PASSWORD.value);
        context.redirect(respondent.signUp(email, name, password).location);
    }
}
