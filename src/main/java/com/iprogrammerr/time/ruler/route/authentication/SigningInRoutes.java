package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.form.FormParams;
import com.iprogrammerr.time.ruler.model.param.SigningInParams;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.authentication.SigningInRespondent;
import com.iprogrammerr.time.ruler.route.Routes;
import com.iprogrammerr.time.ruler.route.day.DayPlanExecutionRoutes;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class SigningInRoutes implements Routes {

    private final Identity<Long> identity;
    private final SigningInRespondent respondent;
    private final DayPlanExecutionRoutes dayRoutes;

    public SigningInRoutes(Identity<Long> identity, SigningInRespondent respondent,
        DayPlanExecutionRoutes dayRoutes) {
        this.identity = identity;
        this.respondent = respondent;
        this.dayRoutes = dayRoutes;
    }

    @Override
    public void init(Javalin app) {
        app.get(SigningInRespondent.SIGN_IN, this::renderSignInOrRedirect);
        app.post(SigningInRespondent.SIGN_IN, this::signIn);
    }

    private void renderSignInOrRedirect(Context context) {
        if (identity.isValid(context.req)) {
            dayRoutes.redirectToToday(context);
        } else {
            SigningInParams params = SigningInParams.fromQuery(context.queryParamMap());
            context.html(respondent.signInPage(params).html);
        }
    }

    private void signIn(Context context) {
        Redirection redirection = respondent.signIn(context.req, context.formParam(FormParams.EMAIL_NAME),
            context.formParam(FormParams.PASSWORD));
        context.redirect(redirection.location);
    }
}
