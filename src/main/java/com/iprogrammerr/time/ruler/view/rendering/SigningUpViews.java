package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class SigningUpViews {

    private static final String SIGN_IN_URL_TEMPLATE = "signInUrl";
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

    public String view() {
        Map<String, Object> params = new HashMap<>();
        params.put(SIGN_IN_URL_TEMPLATE, signInUrl);
        return templates.rendered(formName, params);
    }

    public String successView() {
        return templates.rendered(successName);
    }
}
