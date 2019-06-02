package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateParsing;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

public class DayPlanRespondent {

    public static final String DAY_PLAN = "plan/day";
    private final Identity<Long> identity;
    private final DayPlanViews views;
    private final ActivitiesSearch activities;
    private final LimitedDate limitedDate;
    private final DateParsing parsing;
    private final ServerClientDates serverClientDates;

    public DayPlanRespondent(Identity<Long> identity, DayPlanViews views, ActivitiesSearch activities,
        LimitedDate limitedDate, DateParsing parsing, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.activities = activities;
        this.limitedDate = limitedDate;
        this.parsing = parsing;
        this.serverClientDates = serverClientDates;
    }

    public HtmlResponse dayPlanPage(HttpServletRequest request, String date) {
        Instant requestedDate = limitedDate.fromString(date);
        List<Activity> dayActivities = activities.userDayPlannedActivities(identity.value(request),
            new SmartDate(requestedDate).dayBeginningWithOffset(serverClientDates.clientUtcOffset(request)));
        return new HtmlResponse(views.view(requestedDate, d -> serverClientDates.clientDate(request, d),
            dayActivities));
    }

    public Redirection redirection(Instant date) {
        return new Redirection(new UrlQueryBuilder().put(QueryParams.DATE, parsing.write(date))
            .build(DAY_PLAN));
    }
}
