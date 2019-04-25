package com.iprogrammerr.time.ruler.respondent.day;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.YearMonthDay;
import com.iprogrammerr.time.ruler.model.rendering.ForListActivity;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanExecutionView;
import io.javalin.Context;
import io.javalin.Javalin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DayPlanExecutionRespondent implements GroupedRespondent {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String DAY_PLAN_EXECUTION = "day-plan-execution";
    private final Identity<Long> identity;
    private final DayPlanExecutionView view;
    private final Activities activities;
    private final DateFormat dateFormat;

    public DayPlanExecutionRespondent(Identity<Long> identity, DayPlanExecutionView view, Activities activities,
        DateFormat dateFormat) {
        this.identity = identity;
        this.view = view;
        this.activities = activities;
        this.dateFormat = dateFormat;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + DAY_PLAN_EXECUTION, this::renderPlanExecution);
    }

    private void renderPlanExecution(Context context) {
        ZonedDateTime requestedDate = requestedDate(context);
        List<Activity> dayActivities = activities
            .ofUserDate(identity.value(context.req), requestedDate.toEpochSecond());
        List<ForListActivity> plannedActivities = new ArrayList<>();
        List<ForListActivity> executedActivities = new ArrayList<>();
        for (Activity a : dayActivities) {
            ForListActivity activity = new ForListActivity(a, TIME_FORMAT);
            if (a.done) {
                executedActivities.add(activity);
            } else {
                plannedActivities.add(activity);
            }
        }
        boolean history = new SmartDate(ZonedDateTime.now(Clock.systemUTC())).dayBeginning() > requestedDate
            .toEpochSecond();
        context.html(view.rendered(history, plannedActivities,
            executedActivities));
    }

    private ZonedDateTime requestedDate(Context context) {
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
        return requestedDate.withDayOfMonth(day);
    }

    public void redirect(Context context, int year, int month, int day) {
        //TODO date?
        //context.redirect(DAY_PLAN);
    }
}
