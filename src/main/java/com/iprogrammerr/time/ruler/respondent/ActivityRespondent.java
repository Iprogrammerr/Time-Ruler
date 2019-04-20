package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRespondent implements GroupedRespondent {

    private static final String ACTIVITY = "activity";
    private static final String ID = "id";
    private final Identity<Long> identity;
    private final ViewsTemplates templates;
    private final DayPlanRespondent dayPlanRespondent;

    public ActivityRespondent(Identity<Long> identity, ViewsTemplates templates, DayPlanRespondent dayPlanRespondent) {
        this.identity = identity;
        this.templates = templates;
        this.dayPlanRespondent = dayPlanRespondent;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITY, this::showEmptyActivity);
        app.get(group + ACTIVITY + "/:id", this::showActivity);
        app.post(group + ACTIVITY, this::createActivity);
        app.post(group + ACTIVITY + "/:id", this::saveActivity);
    }

    private void showEmptyActivity(Context context) {
        templates.render(context, ACTIVITY, new HashMap<>());
    }

    //TODO render with proper params
    private void showActivity(Context context) {
        int id = context.pathParam(ID, Integer.class).get();
        templates.render(context, ACTIVITY, new HashMap<>());
    }

    private void createActivity(Context context) {
        printFormParams(context);
        dayPlanRespondent.redirect(context);
    }

    private void printFormParams(Context context) {
        System.out.println("Printing...");
        for (Map.Entry<String, List<String>> entry : context.formParamMap().entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String value : entry.getValue()) {
                System.out.println(value);
            }
        }
    }

    private void saveActivity(Context context) {
        printFormParams(context);
        dayPlanRespondent.redirect(context);
    }
}
