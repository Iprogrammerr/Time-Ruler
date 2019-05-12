package com.iprogrammerr.time.ruler.route.authentication;

import com.iprogrammerr.time.ruler.respondent.authentication.SigningOutRespondent;
import com.iprogrammerr.time.ruler.route.Routes;
import io.javalin.Javalin;

public class SigningOutRoutes implements Routes {

    private final SigningOutRespondent respondent;

    public SigningOutRoutes(SigningOutRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        app.get(SigningOutRespondent.SIGN_OUT, ctx ->
            ctx.redirect(respondent.signOut(ctx.req).location)
        );
    }
}
