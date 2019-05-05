package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.view.rendering.ErrorViews;
import io.javalin.Context;
import io.javalin.Javalin;

public class ErrorRespondent implements Respondent {

    private static final String CODE = "code";
    private static final String ERROR = "error";
    private static final String ERROR_WITH_CODE = ERROR + "/:" + CODE;
    private final ErrorViews views;

    public ErrorRespondent(ErrorViews views) {
        this.views = views;
    }

    @Override
    public void init(Javalin app) {
        app.get(ERROR_WITH_CODE, this::showError);
        app.exception(Exception.class, this::handleException);
    }

    private void showError(Context context) {
        int code = context.pathParam(CODE, Integer.class).get();
        if (code < 0 || code > ErrorCode.values().length) {
            code = 0;
        }
        context.html(views.view(ErrorCode.values()[code], context.req.getLocale()));
    }

    private void handleException(Exception exception, Context context) {
        ErrorCode error;
        if (ResponseException.class.isAssignableFrom(exception.getClass())) {
            error = ((ResponseException) exception).error;
        } else {
            error = ErrorCode.UNKNOWN;
        }
        //TODO for development purposes only
        exception.printStackTrace();
        redirect(context, error);
    }

    private void redirect(Context context, ErrorCode error) {
        context.redirect(String.format("/%s/%d", ERROR, error.ordinal()));
    }
}
