package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.view.rendering.ErrorViews;

import java.util.Locale;

public class ErrorRespondent {

    public static final String CODE = "code";
    private static final String ERROR = "error";
    public static final String ERROR_WITH_CODE = ERROR + "/:" + CODE;
    private final ErrorViews views;

    public ErrorRespondent(ErrorViews views) {
        this.views = views;
    }

    public HtmlResponse errorPage(int code, Locale locale) {
        if (code < 0 || code > ErrorCode.values().length) {
            code = 0;
        }
        return new HtmlResponse(views.view(ErrorCode.values()[code], locale));
    }

    public Redirection handleException(Exception exception) {
        ErrorCode error;
        if (ResponseException.class.isAssignableFrom(exception.getClass())) {
            error = ((ResponseException) exception).error;
        } else {
            error = ErrorCode.UNKNOWN;
        }
        exception.printStackTrace();
        return new Redirection(String.format("/%s/%d", ERROR, error.ordinal()));
    }
}
