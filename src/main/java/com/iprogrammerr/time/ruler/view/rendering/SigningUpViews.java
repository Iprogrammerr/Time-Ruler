package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningUpViews {

    private static final String SIGN_IN_URL_TEMPLATE = "signInUrl";
    private static final String INVALID_EMAIL_TEMPLATE = "invalidEmail";
    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_PASSWORD = "invalidPassword";
    private static final String EMAIL_TEMPLATE = "email";
    private static final String NAME_TEMPLATE = "name";
    private static final String EMAIL_TAKEN_TEMPLATE = "emailTaken";
    private static final String NAME_TAKEN_TEMPLATE = "nameTaken";
    private final ViewsTemplates templates;
    private final String formName;
    private final String successName;
    private final String signInUrl;

    public SigningUpViews(ViewsTemplates templates, String formName, String successName, String signInUrl) {
        this.templates = templates;
        this.formName = formName;
        this.successName = successName;
        this.signInUrl = signInUrl;
    }

    public SigningUpViews(ViewsTemplates templates, String signInUrl) {
        this(templates, "sign-up", "sign-up-success", signInUrl);
    }

    public String valid() {
        return withSignInUrl(new HashMap<>());
    }

    private String withSignInUrl(Map<String, Object> params) {
        params.put(SIGN_IN_URL_TEMPLATE, signInUrl);
        return templates.rendered(formName, params);
    }

    public String invalid(ValidateableEmail email, ValidateableName name, ValidateablePassword password) {
        Map<String, Object> params = new HashMap<>();
        params.put(INVALID_EMAIL_TEMPLATE, !email.isValid());
        params.put(EMAIL_TEMPLATE, email.isValid() ? email.value() : "");
        params.put(INVALID_NAME_TEMPLATE, !name.isValid());
        params.put(NAME_TEMPLATE, name.isValid() ? name.value() : "");
        params.put(INVALID_PASSWORD, !password.isValid());
        return withSignInUrl(params);
    }

    public String emailTaken(String email, String name) {
        return taken(email, name, true, false);
    }

    private String taken(String email, String name, boolean emailTaken, boolean nameTaken) {
        Map<String, Object> params = new HashMap<>();
        params.put(EMAIL_TEMPLATE, email);
        params.put(NAME_TEMPLATE, name);
        params.put(EMAIL_TAKEN_TEMPLATE, emailTaken);
        params.put(NAME_TAKEN_TEMPLATE, nameTaken);
        return withSignInUrl(params);
    }

    public String nameTaken(String email, String name) {
        return taken(email, name, false, true);
    }

    public String success() {
        return templates.rendered(successName);
    }
}
