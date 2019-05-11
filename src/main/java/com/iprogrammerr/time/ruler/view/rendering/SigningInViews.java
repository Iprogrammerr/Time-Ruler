package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningInViews {

    private static final String EMAIL_NAME_TEMPLATE = "emailName";
    private static final String INVALID_EMAIL_NAME_TEMPLATE = "invalidEmailName";
    private static final String NON_EXISTENT_USER_TEMPLATE = "nonExistentUser";
    private static final String INVALID_PASSWORD_TEMPLATE = "invalidPassword";
    private static final String NOT_USER_PASSWORD_TEMPLATE = "notUserPassword";
    private static final String ACTIVATION_TEMPLATE = "activation";
    private static final String PASSWORD_CHANGE_TEMPLATE = "passwordChange";
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

    public String validView() {
        return withSignUpUrl(new HashMap<>());
    }

    private String withSignUpUrl(Map<String, Object> params) {
        params.put(SIGN_UP_URL_TEMPLATE, signUpUrl);
        return templates.rendered(name, params);
    }

    public String invalidView(String invalidEmailName, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        if (!invalidEmailName.isEmpty()) {
            params.put(EMAIL_NAME_TEMPLATE, invalidEmailName);
            params.put(INVALID_EMAIL_NAME_TEMPLATE, true);
        } else {
            params.put(INVALID_EMAIL_NAME_TEMPLATE, false);
        }
        params.put(INVALID_PASSWORD_TEMPLATE, invalidPassword);
        return withSignUpUrl(params);
    }

    public String nonExistentUserView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_NAME_TEMPLATE, emailName);
        params.put(NON_EXISTENT_USER_TEMPLATE, true);
        return withSignUpUrl(params);
    }

    public String notUserPasswordView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_NAME_TEMPLATE, emailName);
        params.put(NOT_USER_PASSWORD_TEMPLATE, true);
        return withSignUpUrl(params);
    }

    public String withNewPasswordView() {
        return withMessageView(PASSWORD_CHANGE_TEMPLATE);
    }

    public String withFarewellView() {
        return withMessageView(SIGN_OUT_TEMPLATE);
    }

    private String withMessageView(String key) {
        Map<String, Object> params = new HashMap<>();
        params.put(key, true);
        return withSignUpUrl(params);
    }

    public String withActivationCongratulationsView() {
        return withMessageView(ACTIVATION_TEMPLATE);
    }
}
