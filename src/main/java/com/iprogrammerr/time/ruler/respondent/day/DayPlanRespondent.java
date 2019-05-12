package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.date.DateParsing;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import io.javalin.Context;
import io.javalin.Javalin;
import io.javalin.core.util.Header;

import java.time.Instant;

public class DayPlanRespondent implements GroupedRespondent {

    private static final String CACHE_VALUE = "no-store";
    private static final String DATE_PARAM = "date";
    private static final String DAY_PLAN = "plan/day";
    private final Identity<Long> identity;
    private final DayPlanViews views;
    private final ActivitiesSearch activities;
    private final LimitedDate limitedDate;
    private final DateParsing parsing;
    private final ServerClientDates serverClientDates;
    private String redirection;

    public DayPlanRespondent(Identity<Long> identity, DayPlanViews views, ActivitiesSearch activities,
        LimitedDate limitedDate,
        DateParsing parsing, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.parsing = parsing;
        this.serverClientDates = serverClientDates;
        this.redirection = "";
    }

    @Override
    public void init(String group, Javalin app) {
        String withGroup = group + DAY_PLAN;
        app.get(withGroup, this::showDayPlan);
        redirection = "/" + withGroup;
    }

    private void showDayPlan(Context context) {
        Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
        context.header(Header.CACHE_CONTROL, CACHE_VALUE);
        context.html(views.view(date, d -> serverClientDates.clientDate(context.req, d),
            activities.ofUserDatePlanned(identity.value(context.req), date.getEpochSecond())));
    }

    public void redirect(Context context, Instant date) {
        context.redirect(new UrlQueryBuilder().put(DATE_PARAM, parsing.write(date)).build(redirection));
    }

    public Redirection redirection(Instant date) {
        return new Redirection(new UrlQueryBuilder().put(DATE_PARAM, parsing.write(date)).build(redirection));
    }
}
