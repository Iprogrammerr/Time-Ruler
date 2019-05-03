package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Map;

public class ProfileViews {

    private static final String PROFILE = "profile";
    private static final String EMAIL_TEMPLATE = "email";
    private static final String NAME_TEMPLATE = "name";
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
        Map<String, Object> params = new HashMap<>();
        params.put(ActiveTab.KEY, ActiveTab.PROFILE);
        params.put(EMAIL_TEMPLATE, user.email);
        params.put(NAME_TEMPLATE, user.name);
        return templates.rendered(name, params);
    }
}
