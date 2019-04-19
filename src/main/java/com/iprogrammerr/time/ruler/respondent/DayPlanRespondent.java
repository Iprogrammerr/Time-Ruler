package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.SmartDate;
import com.iprogrammerr.time.ruler.model.YearMonthDay;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.rendering.ForListActivity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanRespondent implements GroupedRespondent {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String DAY_PLAN_TEMPLATE = "day-plan";
    private static final String DATE_TEMPLATE_PARAM = "date";
    private static final String ACTIVITIES_TEMPLATE_PARAM = "activities";
    private static final String DAY_PLAN = "plan/day";
    private final Identity<Long> identity;
    private final ViewsTemplates templates;
    private final Activities activities;
    private final DateFormat dateFormat;

    public DayPlanRespondent(Identity<Long> identity, ViewsTemplates templates, Activities activities, DateFormat dateFormat) {
        this.identity = identity;
        this.templates = templates;
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
        Map<String, Object> params = new HashMap<>();
        params.put(DATE_TEMPLATE_PARAM, dateFormat.format(date.toEpochMilli()));
        params.put(ACTIVITIES_TEMPLATE_PARAM, dayActivities(identity.value(context.req), date.getEpochSecond()));
        templates.render(context, DAY_PLAN_TEMPLATE, params);
    }

    private List<ForListActivity> dayActivities(long id, long date) {
        List<Activity> dayActivities = activities.ofUserDate(id, date);
        List<ForListActivity> viewActivities = new ArrayList<>(dayActivities.size());
        for (Activity a : dayActivities) {
            viewActivities.add(new ForListActivity(a, TIME_FORMAT));
        }
        return viewActivities;
    }
}
