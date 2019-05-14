package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.view.ViewsTemplates;

public class WelcomeRespondent {

    public static final String WELCOME = "index";
    private final ViewsTemplates templates;

    public WelcomeRespondent(ViewsTemplates templates) {
        this.templates = templates;
    }

    public HtmlResponse welcomePage() {
        return new HtmlResponse(templates.rendered(WELCOME));
    }
}
