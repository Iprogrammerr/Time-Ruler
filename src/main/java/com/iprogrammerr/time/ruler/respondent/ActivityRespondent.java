package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.HashMap;

public class ActivityRespondent implements GroupedRespondent {

    private static final String ACTIVITY = "activity";
    private final Identity<Long> identity;
    private final ViewsTemplates templates;

    public ActivityRespondent(Identity<Long> identity, ViewsTemplates templates) {
        this.identity = identity;
        this.templates = templates;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITY, this::showActivity);
    }

    //TODO render with proper params
    private void showActivity(Context context) {
        templates.render(context, ACTIVITY, new HashMap<>());
    }
}
