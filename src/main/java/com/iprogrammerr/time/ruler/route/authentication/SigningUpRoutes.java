package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.FormParams;
import com.iprogrammerr.time.ruler.model.QueryParams;
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
        String email = params.stringParam(QueryParams.EMAIL);
        String name = params.stringParam(QueryParams.NAME);
        HtmlResponse response;
        if (email.isEmpty() && name.isEmpty()) {
            response = respondent.signUpPage();
        } else {
            response = respondent.invalidSignUpPage(email, name, params.booleanParam(QueryParams.EMAIL_TAKEN),
                params.booleanParam(QueryParams.NAME_TAKEN), params.booleanParam(QueryParams.INVALID_PASSWORD));
        }
        context.html(response.html);
    }

    private void signUp(Context context) {
        String email = context.formParam(FormParams.EMAIL);
        String name = context.formParam(FormParams.NAME);
        String password = context.formParam(FormParams.PASSWORD);
        context.redirect(respondent.signUp(email, name, password).location);
    }
}
