package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateParsing;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionViews;
import io.javalin.Context;
import io.javalin.Javalin;
import io.javalin.core.util.Header;

import java.time.Instant;
import java.util.List;

public class DayPlanExecutionRespondent implements GroupedRespondent {

    private static final String CACHE_VALUE = "no-store";
    private static final String DATE_PARAM = "date";
    private static final String TODAY = "today";
    private static final String DAY_PLAN_EXECUTION = "day-plan-execution";
    private final Identity<Long> identity;
    private final DayPlanExecutionViews views;
    private final ActivitiesSearch activities;
    private final LimitedDate limitedDate;
    private final DateParsing parsing;
    private final ServerClientDates serverClientDates;
    private String todayRedirect;
    private String dayRedirect;

    public DayPlanExecutionRespondent(Identity<Long> identity, DayPlanExecutionViews views, ActivitiesSearch activities,
        LimitedDate limitedDate, DateParsing parsing, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.parsing = parsing;
        this.serverClientDates = serverClientDates;
        this.todayRedirect = "";
        this.dayRedirect = "";
    }

    @Override
    public void init(String group, Javalin app) {
        String todayWithGroup = group + TODAY;
        String dayWithGroup = group + DAY_PLAN_EXECUTION;
        app.get(todayWithGroup, this::renderPlanExecution);
        app.get(dayWithGroup, this::renderPlanExecution);
        todayRedirect = "/" + todayWithGroup;
        dayRedirect = "/" + dayWithGroup;
    }

    private void renderPlanExecution(Context context) {
        Instant clientNow = serverClientDates.clientDate(context.req);
        Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""), clientNow);
        List<Activity> dayActivities = activities.ofUserDate(identity.value(context.req),
            date.getEpochSecond());
        boolean history = new SmartDate(clientNow).dayBeginning() > date.getEpochSecond();
        context.html(views.view(date, history, dayActivities,
            d -> serverClientDates.clientDate(context.req, d)));
        context.header(Header.CACHE_CONTROL, CACHE_VALUE);
    }

    public void redirect(Context context) {
        context.redirect(todayRedirect);
    }

    public Redirection redirection() {
        return new Redirection(todayRedirect);
    }

    public void redirect(Context context, Instant date) {
        context.redirect(new UrlQueryBuilder().put(DATE_PARAM, parsing.write(date)).build(dayRedirect));
    }
}
