package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetViews {

    public final String emailSendName;
    public final String changePasswordName;
    private final ViewsTemplates templates;

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
        params.put(TemplatesParams.LINK_SENT, true);
        params.put(TemplatesParams.EMAIL, email);
        return templates.rendered(emailSendName, params);
    }

    public String invalidEmailView(ValidateableEmail email) {
        Map<String, Object> params = new HashMap<>();
        if (email.isValid()) {
            params.put(TemplatesParams.UNKNOWN_EMAIL, true);
        } else {
            params.put(TemplatesParams.INVALID_EMAIL, true);
        }
        params.put(TemplatesParams.EMAIL, email.value());
        return templates.rendered(emailSendName, params);
    }

    public String inactiveAccountView(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, email);
        params.put(TemplatesParams.INACTIVE_ACCOUNT, true);
        return templates.rendered(emailSendName, params);
    }

    public String changePasswordView(String passwordResetUrl, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.PASSWORD_RESET_URL, passwordResetUrl);
        params.put(TemplatesParams.INVALID_PASSWORD, invalidPassword);
        return templates.rendered(changePasswordName, params);
    }
}
