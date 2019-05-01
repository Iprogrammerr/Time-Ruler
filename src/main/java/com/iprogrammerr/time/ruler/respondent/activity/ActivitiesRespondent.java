package com.iprogrammerr.time.ruler.respondent.activity;

import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.ArrayList;

public class ActivitiesRespondent implements GroupedRespondent {

    private static final String ACTIVITIES = "activities";
    private final ActivitiesViews views;

    public ActivitiesRespondent(ActivitiesViews views) {
        this.views = views;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITIES, this::showPage);
    }

    //TODO give proper params to view
    private void showPage(Context context) {
        context.html(views.view(false, new ArrayList<>()));
    }
}
