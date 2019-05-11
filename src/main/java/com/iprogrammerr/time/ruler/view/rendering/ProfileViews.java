package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class ProfileViews {

    private static final String EMAIL_TEMPLATE = "email";
    private static final String EMAIL_CHANGED = "emailChanged";
    private static final String INVALID_EMAIL_TEMPLATE = "invalidEmail";
    private static final String USED_EMAIL_TEMPLATE = "usedEmail";
    private static final String NAME_TEMPLATE = "name";
    private static final String NAME_CHANGED = "nameChanged";
    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String USED_NAME_TEMPLATE = "usedName";
    private static final String SHOW_PASSWORDS_TEMPLATE = "showPasswords";
    private static final String INVALID_OLD_PASSWORD_TEMPLATE = "invalidOldPassword";
    private static final String NOT_USER_PASSWORD_TEMPLATE = "notUserPassword";
    private static final String INVALID_NEW_PASSWORD_TEMPLATE = "invalidNewPassword";
    private final ViewsTemplates templates;
    private final String name;

    public ProfileViews(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public ProfileViews(ViewsTemplates templates) {
        this(templates, "profile");
    }

    public String view(User user) {
        return view(user, false, false);
    }

    private String view(User user, boolean emailChanged, boolean nameChanged) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_TEMPLATE, user.email);
        params.put(NAME_TEMPLATE, user.name);
        params.put(EMAIL_CHANGED, emailChanged);
        params.put(NAME_CHANGED, nameChanged);
        return withActiveTabSet(params);
    }

    private String withActiveTabSet(Map<String, Object> params) {
        params.put(ActiveTab.KEY, ActiveTab.PROFILE);
        return templates.rendered(name, params);
    }

    public String emailChangedView(User user) {
        return view(user, true, false);
    }

    public String nameChangedView(User user) {
        return view(user, false, true);
    }

    public String invalidEmailView(ValidateableEmail email, boolean usedEmail, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_TEMPLATE, email.value());
        params.put(INVALID_EMAIL_TEMPLATE, !email.isValid());
        params.put(USED_EMAIL_TEMPLATE, usedEmail);
        params.put(NAME_TEMPLATE, name);
        return withActiveTabSet(params);
    }

    public String invalidNameView(ValidateableName name, boolean usedName, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put(NAME_TEMPLATE, name.value());
        params.put(INVALID_NAME_TEMPLATE, !name.isValid());
        params.put(USED_NAME_TEMPLATE, usedName);
        params.put(EMAIL_TEMPLATE, email);
        return withActiveTabSet(params);
    }

    public String invalidPasswordView(User user, boolean invalidOldPassword, boolean notUserPassword,
        boolean invalidNewPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_TEMPLATE, user.email);
        params.put(NAME_TEMPLATE, user.name);
        params.put(SHOW_PASSWORDS_TEMPLATE, true);
        params.put(INVALID_OLD_PASSWORD_TEMPLATE, invalidOldPassword);
        params.put(NOT_USER_PASSWORD_TEMPLATE, notUserPassword);
        params.put(INVALID_NEW_PASSWORD_TEMPLATE, invalidNewPassword);
        return withActiveTabSet(params);
    }
}
