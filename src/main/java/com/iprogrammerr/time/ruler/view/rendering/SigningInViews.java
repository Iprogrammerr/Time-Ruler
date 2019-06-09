package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningInViews {

    public final String name;
    public final String signUpUrl;
    public final String passwordResetUrl;
    private final ViewsTemplates templates;

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
        params.put(TemplatesParams.SIGN_UP_URL, signUpUrl);
        params.put(TemplatesParams.PASSWORD_RESET_URL, passwordResetUrl);
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

    private String invalidView(String emailName, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL_NAME, emailName);
        boolean invalidEmailName;
        if (emailName.contains("@")) {
            invalidEmailName = !new ValidateableEmail(emailName).isValid();
        } else {
            invalidEmailName = !new ValidateableName(emailName).isValid();
        }
        params.put(TemplatesParams.INVALID_EMAIL_NAME, invalidEmailName);
        params.put(TemplatesParams.INVALID_PASSWORD, invalidPassword);
        return withUrls(params);
    }

    private String nonExistentUserView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL_NAME, emailName);
        params.put(TemplatesParams.NON_EXISTENT_USER, true);
        return withUrls(params);
    }

    private String notUserPasswordView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL_NAME, emailName);
        params.put(TemplatesParams.NOT_USER_PASSWORD, true);
        return withUrls(params);
    }

    private String notActiveUserView(String emailName) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL_NAME, emailName);
        params.put(TemplatesParams.INACTIVE_ACCOUNT, true);
        return withUrls(params);
    }

    public String withNewPasswordView() {
        return withMessageView(TemplatesParams.PASSWORD_CHANGE);
    }

    public String withFarewellView() {
        return withMessageView(TemplatesParams.SIGN_OUT);
    }

    private String withMessageView(String key) {
        Map<String, Object> params = new HashMap<>();
        params.put(key, true);
        return withUrls(params);
    }

    public String withActivationCongratulationsView() {
        return withMessageView(TemplatesParams.ACTIVATION);
    }
}
