package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningUpViews {

    public final String formName;
    public final String successName;
    public final String signInUrl;
    private final ViewsTemplates templates;

    public SigningUpViews(ViewsTemplates templates, String formName, String successName, String signInUrl) {
        this.templates = templates;
        this.formName = formName;
        this.successName = successName;
        this.signInUrl = signInUrl;
    }

    public SigningUpViews(ViewsTemplates templates, String signInUrl) {
        this(templates, "sign-up", "sign-up-success", signInUrl);
    }

    public String view() {
        return withSignInUrl(new HashMap<>());
    }

    private String withSignInUrl(Map<String, Object> params) {
        params.put(TemplatesParams.SIGN_IN_URL, signInUrl);
        return templates.rendered(formName, params);
    }

    public String invalidView(ValidateableEmail email, ValidateableName name, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.INVALID_EMAIL, !email.isValid());
        params.put(TemplatesParams.EMAIL, email.value());
        params.put(TemplatesParams.INVALID_NAME, !name.isValid());
        params.put(TemplatesParams.NAME, name.value());
        params.put(TemplatesParams.INVALID_PASSWORD, invalidPassword);
        return withSignInUrl(params);
    }

    public String takenView(String email, String name, boolean emailTaken, boolean nameTaken) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, email);
        params.put(TemplatesParams.NAME, name);
        params.put(TemplatesParams.EMAIL_TAKEN, emailTaken);
        params.put(TemplatesParams.NAME_TAKEN, nameTaken);
        return withSignInUrl(params);
    }

    public String successView() {
        return templates.rendered(successName);
    }
}
