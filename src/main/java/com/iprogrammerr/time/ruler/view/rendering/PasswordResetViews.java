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
    private final ViewsTemplates templates;
    private final String name;

    public PasswordResetViews(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public PasswordResetViews(ViewsTemplates templates) {
        this(templates, "password-reset");
    }

    public String view() {
        return templates.rendered(name);
    }

    public String emailSentView(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put(LINK_SENT_TEMPLATE, true);
        params.put(EMAIL_TEMPLATE, email);
        return templates.rendered(name, params);
    }

    public String invalidEmailView(ValidateableEmail email) {
        Map<String, Object> params = new HashMap<>();
        if (email.isValid()) {
            params.put(UNKNOWN_EMAIL_TEMPLATE, true);
        } else {
            params.put(INVALID_EMAIL_TEMPLATE, true);
        }
        params.put(EMAIL_TEMPLATE, email.value());
        return templates.rendered(name, params);
    }
}
