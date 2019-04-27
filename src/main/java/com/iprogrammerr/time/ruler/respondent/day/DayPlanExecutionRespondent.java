package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionView;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DayPlanExecutionRespondent implements GroupedRespondent {

    private static final String DATE_PARAM = "date";
    private static final String DAY_PLAN_EXECUTION = "day-plan-execution";
    private final Identity<Long> identity;
    private final DayPlanExecutionView view;
    private final Activities activities;
    private final LimitedDate limitedDate;
    private final DateTimeFormatting formatting;

    public DayPlanExecutionRespondent(Identity<Long> identity, DayPlanExecutionView view, Activities activities,
        LimitedDate limitedDate, DateTimeFormatting formatting) {
        this.identity = identity;
        this.view = view;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.formatting = formatting;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + DAY_PLAN_EXECUTION, this::renderPlanExecution);
    }

    private void renderPlanExecution(Context context) {
        Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
        List<Activity> dayActivities = activities.ofUserDate(identity.value(context.req),
            date.getEpochSecond());
        List<ForViewActivity> plannedActivities = new ArrayList<>();
        List<ForViewActivity> executedActivities = new ArrayList<>();
        for (Activity a : dayActivities) {
            ForViewActivity activity = new ForViewActivity(a.name, formatting.timeFromSeconds(a.startTime),
                formatting.timeFromSeconds(a.endTime));
            if (a.done) {
                executedActivities.add(activity);
            } else {
                plannedActivities.add(activity);
            }
        }
        boolean history = new SmartDate().dayBeginning() > date.getEpochSecond();
        context.html(view.rendered(history, plannedActivities,
            executedActivities));
    }

    public void redirect(Context context, int year, int month, int day) {
        //TODO date?
        //context.redirect(DAY_PLAN);
    }
}
