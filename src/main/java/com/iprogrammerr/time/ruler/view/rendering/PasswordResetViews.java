package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetViews {

    private static final String LINK_SENT_TEMPLATE = "linkSent";
    private static final String INVALID_EMAIL_TEMPLATE = "invalidEmail";
    private static final String UNKNOWN_EMAIL_TEMPLATE = "unknownEmail";
    private static final String EMAIL_TEMPLATE = "email";
    private static final String INVALID_PASSWORD_TEMPLATE = "invalidPassword";
    private static final String PASWORD_RESET_URL_TEMPLATE = "passwordResetUrl";
    private final ViewsTemplates templates;
    private final String emailSendName;
    private final String changePasswordName;

    public PasswordResetViews(ViewsTemplates templates, String emailSendName, String changePasswordName) {
        this.templates = templates;
        this.emailSendName = emailSendName;
        this.changePasswordName = changePasswordName;
    }

    public PasswordResetViews(ViewsTemplates templates) {
        this(templates, "password-reset", "password-reset-form");
    }

    public String sendEmailView() {
        return templates.rendered(emailSendName);
    }

    public String emailSentView(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put(LINK_SENT_TEMPLATE, true);
        params.put(EMAIL_TEMPLATE, email);
        return templates.rendered(emailSendName, params);
    }

    public String invalidEmailView(ValidateableEmail email) {
        Map<String, Object> params = new HashMap<>();
        if (email.isValid()) {
            params.put(UNKNOWN_EMAIL_TEMPLATE, true);
        } else {
            params.put(INVALID_EMAIL_TEMPLATE, true);
        }
        params.put(EMAIL_TEMPLATE, email.value());
        return templates.rendered(emailSendName, params);
    }

    public String changePasswordView(String passwordResetUrl, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(PASWORD_RESET_URL_TEMPLATE, passwordResetUrl);
        params.put(INVALID_PASSWORD_TEMPLATE, invalidPassword);
        return templates.rendered(changePasswordName, params);
    }
}
