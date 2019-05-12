package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.FormKey;
import com.iprogrammerr.time.ruler.model.QueryParamKey;
import com.iprogrammerr.time.ruler.model.RequestParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.authentication.PasswordResetRespondent;
import com.iprogrammerr.time.ruler.route.Routes;
import io.javalin.Context;
import io.javalin.Javalin;

public class PasswordResetRoutes implements Routes {

    private final PasswordResetRespondent respondent;

    public PasswordResetRoutes(PasswordResetRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        app.get(PasswordResetRespondent.PASSWORD_RESET, this::showPasswordReset);
        app.get(PasswordResetRespondent.PASSWORD_RESET_FORM, this::showPasswordResetForm);
        app.post(PasswordResetRespondent.PASSWORD_RESET, this::sentPasswordResetEmail);
        app.post(PasswordResetRespondent.PASSWORD_RESET_FORM, this::resetPassword);
    }

    private void showPasswordReset(Context context) {
        RequestParams params = new RequestParams(context);
        HtmlResponse response = respondent.passwordResetPage(params.stringParam(QueryParamKey.EMAIL),
            params.booleanParam(QueryParamKey.EMAIL_SENT), params.booleanParam(QueryParamKey.INACTIVE_ACCOUNT));
        context.html(response.html);
    }

    private void showPasswordResetForm(Context context) {
        RequestParams params = new RequestParams(context);
        HtmlResponse response = respondent.passwordResetForm(params.stringParam(QueryParamKey.EMAIL),
            params.stringParam(QueryParamKey.HASH), passwordResetUrl(context));
        context.html(response.html);
    }

    private String passwordResetUrl(Context context) {
        return context.path() + "?" + context.queryString();
    }

    private void sentPasswordResetEmail(Context context) {
        Redirection redirection = respondent.sentPasswordResetEmail(context.formParam(FormKey.PASSWORD.value));
        context.redirect(redirection.location);
    }

    private void resetPassword(Context context) {
        RequestParams params = new RequestParams(context);
        Redirection redirection = respondent.resetPassword(params.stringParam(QueryParamKey.EMAIL),
            params.stringParam(QueryParamKey.HASH), context.formParam(FormKey.PASSWORD.value),
            passwordResetUrl(context));
        context.redirect(redirection.location);
    }
}
