package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ErrorsTranslation;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ErrorViews {

    private static final String ERROR_TEMPLATE = "error";
    private final ViewsTemplates templates;
    private final ErrorsTranslation translation;
    private final String name;

    public ErrorViews(ViewsTemplates templates, ErrorsTranslation translation, String name) {
        this.templates = templates;
        this.translation = translation;
        this.name = name;
    }

    public ErrorViews(ViewsTemplates templates, ErrorsTranslation translation) {
        this(templates, translation, "error");
    }

    public String view(ErrorCode code, Locale locale) {
        Map<String, Object> params = new HashMap<>();
        params.put(ERROR_TEMPLATE, translation.translated(code, locale));
        return templates.rendered(name, params);
    }
}
