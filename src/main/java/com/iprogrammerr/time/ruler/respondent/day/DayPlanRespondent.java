package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.date.DateParsing;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Instant;

public class DayPlanRespondent implements GroupedRespondent {

    private static final String DATE_PARAM = "date";
    private static final String DAY_PLAN = "plan/day";
    private final Identity<Long> identity;
    private final DayPlanViews views;
    private final Activities activities;
    private final LimitedDate limitedDate;
    private final DateParsing parsing;
    private String redirect;

    public DayPlanRespondent(Identity<Long> identity, DayPlanViews views, Activities activities, LimitedDate limitedDate,
        DateParsing parsing) {
        this.identity = identity;
        this.views = views;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.parsing = parsing;
        this.redirect = "";
    }

    @Override
    public void init(String group, Javalin app) {
        String withGroup = group + DAY_PLAN;
        app.get(withGroup, this::showDayPlan);
        redirect = "/" + withGroup;
    }

    private void showDayPlan(Context context) {
        Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
        context.html(views.view(date, activities.ofUserDatePlanned(identity.value(context.req),
            date.getEpochSecond())));
    }

    public void redirect(Context context, Instant date) {
        context.redirect(new UrlQueryBuilder().put(DATE_PARAM, parsing.write(date)).build(redirect));
    }
}
