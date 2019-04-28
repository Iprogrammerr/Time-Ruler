package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.DateParsing;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Instant;
import java.util.List;

public class DayPlanExecutionRespondent implements GroupedRespondent {

    private static final String DATE_PARAM = "date";
    private static final String DAY_PLAN_EXECUTION = "day-plan-execution";
    private final Identity<Long> identity;
    private final DayPlanExecutionViews views;
    private final Activities activities;
    private final LimitedDate limitedDate;
    private final DateParsing parsing;
    private final ServerClientDates serverClientDates;
    private String redirect;

    public DayPlanExecutionRespondent(Identity<Long> identity, DayPlanExecutionViews views, Activities activities,
        LimitedDate limitedDate, DateParsing parsing, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.parsing = parsing;
        this.serverClientDates = serverClientDates;
        this.redirect = "";
    }

    @Override
    public void init(String group, Javalin app) {
        String withGroup = group + DAY_PLAN_EXECUTION;
        app.get(withGroup, this::renderPlanExecution);
        redirect = "/" + withGroup;
    }

    private void renderPlanExecution(Context context) {
        Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
        List<Activity> dayActivities = activities.ofUserDate(identity.value(context.req),
            date.getEpochSecond());
        boolean history = new SmartDate().dayBeginning() > date.getEpochSecond();
        context.html(views.view(history, dayActivities,
            d -> serverClientDates.clientDate(context.req, d)));
    }

    public void redirect(Context context, Instant date) {
        context.redirect(new UrlQueryBuilder().put(DATE_PARAM, parsing.write(date)).build(redirect));
    }
}
