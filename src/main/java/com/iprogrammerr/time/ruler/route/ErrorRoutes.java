package com.iprogrammerr.time.ruler.route;

import com.iprogrammerr.time.ruler.respondent.ErrorRespondent;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ErrorRoutes implements Routes {

    private final ErrorRespondent respondent;

    public ErrorRoutes(ErrorRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        app.get(ErrorRespondent.ERROR_WITH_CODE, this::showError);
        app.exception(Exception.class, this::handleException);
    }

    private void showError(Context context) {
        int code = context.pathParam(ErrorRespondent.CODE, Integer.class).get();
        context.html(respondent.errorPage(code, context.req.getLocale()).html);
    }

    private void handleException(Exception exception, Context context) {
        context.redirect(respondent.handleException(exception).location);
    }
}
