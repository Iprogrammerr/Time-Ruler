package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningInViews {

    private static final String EMAIL_NAME_TEMPLATE = "emailName";
    private static final String INVALID_EMAIL_NAME_TEMPLATE = "invalidEmailName";
    private static final String NON_EXISTENT_USER_TEMPLATE = "nonExistentUser";
    private static final String INVALID_PASSWORD_TEMPLATE = "invalidPassword";
    private static final String NOT_USER_PASSWORD_TEMPLATE = "notUserPassword";
    private static final String INACTIVE_ACCOUNT_TEMPLATE = "inactiveAccount";
    private static final String ACTIVATION_TEMPLATE = "activation";
    private static final String PASSWORD_CHANGE_TEMPLATE = "passwordChange";
    private static final String SIGN_OUT_TEMPLATE = "signOut";
    private static final String SIGN_UP_URL_TEMPLATE = "signUpUrl";
    private static final String PASSWORD_RESET_URL_TEMPLATE = "passwordResetUrl";
    private final ViewsTemplates templates;
    private final String name;
    private final String signUpUrl;
    private final String passwordResetUrl;

    public SigningInViews(ViewsTemplates templates, String name, String signUpUrl, String passwordResetUrl) {
        this.templates = templates;
        this.name = name;
        this.signUpUrl = signUpUrl;
        this.passwordResetUrl = passwordResetUrl;
    }

    public SigningInViews(ViewsTemplates templates, String signUpUrl, String passwordResetUrl) {
        this(templates, "sign-in", signUpUrl, passwordResetUrl);
    }

    public String validView() {
        return withUrls(new HashMap<>());
    }

    private String withUrls(Map<String, Object> params) {
        params.put(SIGN_UP_URL_TEMPLATE, signUpUrl);
        params.put(PASSWORD_RESET_URL_TEMPLATE, passwordResetUrl);
        return templates.rendered(name, params);
    }

    public String invalidView(String emailName, boolean nonExistentUser, boolean inactiveAccount,
        boolean notUserPassword, boolean invalidPassword) {
        String view;
        if (nonExistentUser) {
            view = nonExistentUserView(emailName);
        } else if (inactiveAccount) {
            view = notActiveUserView(emailName);
        } else if (notUserPassword) {
            view = notUserPasswordView(emailName);
        } else {
            view = invalidView(emailName, invalidPassword);
        }
        return view;
    }

    private String invalidView(String invalidEmailName, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        if (!invalidEmailName.isEmpty()) {
            params.put(EMAIL_NAME_TEMPLATE, invalidEmailName);
            boolean valid = invalidEmailName.contains("@") ?
                new ValidateableEmail(invalidEmailName).isValid() :
                new ValidateableName(invalidEmailName).isValid();
            params.put(INVALID_EMAIL_NAME_TEMPLATE, valid);
        } else {
            params.put(INVALID_EMAIL_NAME_TEMPLATE, false);
        }
        params.put(INVALID_PASSWORD_TEMPLATE, invalidPassword);
        return withUrls(params);
    }

    private String nonExistentUserView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_NAME_TEMPLATE, emailName);
        params.put(NON_EXISTENT_USER_TEMPLATE, true);
        return withUrls(params);
    }

    private String notUserPasswordView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_NAME_TEMPLATE, emailName);
        params.put(NOT_USER_PASSWORD_TEMPLATE, true);
        return withUrls(params);
    }

    private String notActiveUserView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_NAME_TEMPLATE, emailName);
        params.put(INACTIVE_ACCOUNT_TEMPLATE, true);
        return withUrls(params);
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
        return withUrls(params);
    }

    public String withActivationCongratulationsView() {
        return withMessageView(ACTIVATION_TEMPLATE);
    }
}
