package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class ProfileViews {

    public final String name;
    private final ViewsTemplates templates;

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
        params.put(TemplatesParams.EMAIL, user.email);
        params.put(TemplatesParams.NAME, user.name);
        params.put(TemplatesParams.EMAIL_CHANGED, emailChanged);
        params.put(TemplatesParams.NAME_CHANGED, nameChanged);
        return withActiveTabSet(params);
    }

    private String withActiveTabSet(Map<String, Object> params) {
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.PROFILE);
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
        params.put(TemplatesParams.EMAIL, email.value());
        params.put(TemplatesParams.INVALID_EMAIL, !email.isValid());
        params.put(TemplatesParams.USED_EMAIL, usedEmail);
        params.put(TemplatesParams.NAME, name);
        return withActiveTabSet(params);
    }

    public String invalidNameView(ValidateableName name, boolean usedName, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.NAME, name.value());
        params.put(TemplatesParams.INVALID_NAME, !name.isValid());
        params.put(TemplatesParams.USED_NAME, usedName);
        params.put(TemplatesParams.EMAIL, email);
        return withActiveTabSet(params);
    }

    public String invalidPasswordView(User user, boolean invalidOldPassword, boolean notUserPassword,
        boolean invalidNewPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, user.email);
        params.put(TemplatesParams.NAME, user.name);
        params.put(TemplatesParams.SHOW_PASSWORDS, true);
        params.put(TemplatesParams.INVALID_OLD_PASSWORD, invalidOldPassword);
        params.put(TemplatesParams.NOT_USER_PASSWORD, notUserPassword);
        params.put(TemplatesParams.INVALID_NEW_PASSWORD, invalidNewPassword);
        return withActiveTabSet(params);
    }
}
