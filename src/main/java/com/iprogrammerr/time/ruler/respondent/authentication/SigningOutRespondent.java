package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.view.user.SigningInView;
import io.javalin.Context;
import io.javalin.Javalin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class SigningOutRespondent implements Respondent {

    private static final String SIGN_OUT = "sign-out";
    private final SigningInView signInView;

    public SigningOutRespondent(SigningInView signInView) {
        this.signInView = signInView;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_OUT, this::signOut);
    }

    public void signOut(Context context) {
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
        context.html(signInView.withFarewell());
    }
}
