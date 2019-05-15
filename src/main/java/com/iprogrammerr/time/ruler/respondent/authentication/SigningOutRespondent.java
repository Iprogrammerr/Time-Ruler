package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.respondent.Redirection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SigningOutRespondent {

    public static final String SIGN_OUT = "sign-out";
    private final SigningInRespondent respondent;

    public SigningOutRespondent(SigningInRespondent respondent) {
        this.respondent = respondent;
    }

    public Redirection signOut(HttpServletRequest request) {
        clearData(request);
        return respondent.withFarewellRedirection();
    }

    private void clearData(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                c.setMaxAge(0);
            }
        }
    }

    public Redirection newPasswordSignOut(HttpServletRequest request) {
        clearData(request);
        return respondent.withNewPasswordRedirection();
    }
}
