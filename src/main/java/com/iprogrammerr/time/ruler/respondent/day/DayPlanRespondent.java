package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.YearMonthDay;
import com.iprogrammerr.time.ruler.model.rendering.ForViewActivity;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanView;
import io.javalin.Context;
import io.javalin.Javalin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

//TODO remove reading year/month/day code duplication
public class DayPlanRespondent implements GroupedRespondent {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String DAY_PLAN = "plan/day";
    private final Identity<Long> identity;
    private final DayPlanView view;
    private final Activities activities;
    private final DateFormat dateFormat;

    public DayPlanRespondent(Identity<Long> identity, DayPlanView view, Activities activities, DateFormat dateFormat) {
        this.identity = identity;
        this.view = view;
        this.activities = activities;
        this.dateFormat = dateFormat;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + DAY_PLAN, this::showDayPlan);
    }

    private void showDayPlan(Context context) {
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        YearMonthDay yearMonthDay = new YearMonthDay(context.queryParamMap(), now.getYear() + MAX_YEAR_OFFSET_VALUE);
        int year = yearMonthDay.year(now.getYear());
        if (year < now.getYear()) {
            year = now.getYear();
        }
        int month = yearMonthDay.month(now.getMonthValue());
        ZonedDateTime requestedDate = new SmartDate(now).ofYearMonth(year, month);
        int day = yearMonthDay.day(now.getDayOfMonth());
        int maxDay = requestedDate.toLocalDate().lengthOfMonth();
        if (day > maxDay) {
            day = maxDay;
        }
        Instant date = Instant.ofEpochSecond(requestedDate.withDayOfMonth(day).toEpochSecond());
        context.html(view.rendered(dateFormat.format(date.toEpochMilli()),
            plannedActivities(identity.value(context.req), date.getEpochSecond())));
    }

    private List<ForViewActivity> plannedActivities(long id, long date) {
        List<Activity> dayActivities = activities.ofUserDatePlanned(id, date);
        List<ForViewActivity> viewActivities = new ArrayList<>(dayActivities.size());
        for (Activity a : dayActivities) {
            viewActivities.add(new ForViewActivity(a, TIME_FORMAT));
        }
        return viewActivities;
    }

    public void redirect(Context context, int year, int month, int day) {
        //TODO date?
        context.redirect(DAY_PLAN);
    }
}
