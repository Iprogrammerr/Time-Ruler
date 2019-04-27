package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanView;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DayPlanRespondent implements GroupedRespondent {

    private static final String DATE_PARAM = "date";
    private static final String DAY_PLAN = "plan/day";
    private final Identity<Long> identity;
    private final DayPlanView view;
    private final Activities activities;
    private final LimitedDate limitedDate;
    private final DateTimeFormatting formatting;

    public DayPlanRespondent(Identity<Long> identity, DayPlanView view, Activities activities, LimitedDate limitedDate,
        DateTimeFormatting formatting) {
        this.identity = identity;
        this.view = view;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.formatting = formatting;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + DAY_PLAN, this::showDayPlan);
    }

    private void showDayPlan(Context context) {
        Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
        context.html(view.rendered(formatting.dateFromSeconds(date.getEpochSecond()),
            plannedActivities(identity.value(context.req), date.getEpochSecond())));
    }

    private List<ForViewActivity> plannedActivities(long id, long date) {
        List<Activity> dayActivities = activities.ofUserDatePlanned(id, date);
        List<ForViewActivity> viewActivities = new ArrayList<>(dayActivities.size());
        for (Activity a : dayActivities) {
            viewActivities.add(new ForViewActivity(a.name, formatting.timeFromSeconds(a.startTime),
                formatting.timeFromSeconds(a.endTime)));
        }
        return viewActivities;
    }

    public void redirect(Context context, String date) {
        context.redirect(new StringBuilder(DAY_PLAN).append("?").append(DATE_PARAM)
            .append("=").append(date).toString());
    }
}
