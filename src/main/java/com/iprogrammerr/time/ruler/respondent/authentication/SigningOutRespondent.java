package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.respondent.Respondent;
import io.javalin.Context;
import io.javalin.Javalin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class SigningOutRespondent implements Respondent {

    private static final String SIGN_OUT = "sign-out";
    private final SigningInRespondent respondent;

    public SigningOutRespondent(SigningInRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_OUT, this::signOut);
    }

    private void signOut(Context context) {
        clearData(context);
        respondent.redirectWithFarewell(context);
    }

    private void clearData(Context context) {
        HttpSession session = context.req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie[] cookies = context.req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                c.setMaxAge(0);
                context.cookie(c);
            }
        }
    }

    public void newPasswordSignOut(Context context) {
        clearData(context);
        respondent.redirectWithNewPassword(context);
    }
}
