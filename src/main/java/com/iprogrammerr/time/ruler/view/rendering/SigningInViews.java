package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningInViews {

    private static final String EMAIL_LOGIN_TEMPLATE = "emailLogin";
    private static final String INVALID_EMAIL_LOGIN_TEMPLATE = "invalidEmailLogin";
    private static final String INVALID_PASSWORD_TEMPLATE = "invalidPassword";
    private static final String ACTIVATION_TEMPLATE = "activation";
    private static final String SIGN_OUT_TEMPLATE = "signOut";
    private static final String SIGN_UP_URL_TEMPLATE = "signUpUrl";
    private final ViewsTemplates templates;
    private final String name;
    private final String signUpUrl;

    public SigningInViews(ViewsTemplates templates, String name, String signUpUrl) {
        this.templates = templates;
        this.name = name;
        this.signUpUrl = signUpUrl;
    }

    public SigningInViews(ViewsTemplates templates, String signUpUrl) {
        this(templates, "sign-in", signUpUrl);
    }

    public String valid() {
        return withSignUpUrl(new HashMap<>());
    }

    private String withSignUpUrl(Map<String, Object> params) {
        params.put(SIGN_UP_URL_TEMPLATE, signUpUrl);
        return templates.rendered(name, params);
    }

    public String invalid(String invalidEmailLogin, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        if (!invalidEmailLogin.isEmpty()) {
            params.put(EMAIL_LOGIN_TEMPLATE, invalidEmailLogin);
            params.put(INVALID_EMAIL_LOGIN_TEMPLATE, true);
        } else {
            params.put(INVALID_EMAIL_LOGIN_TEMPLATE, false);
        }
        params.put(INVALID_PASSWORD_TEMPLATE, invalidPassword);
        return withSignUpUrl(params);
    }

    public String withFarewell() {
        return withMessage(true);
    }

    private String withMessage(boolean farewell) {
        Map<String, Object> params = new HashMap<>();
        params.put(ACTIVATION_TEMPLATE, !farewell);
        params.put(SIGN_OUT_TEMPLATE, farewell);
        return withSignUpUrl(params);
    }

    public String withActivationCongratulations() {
        return withMessage(false);
    }
}
