package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.SmartDate;
import com.iprogrammerr.time.ruler.model.YearMonthDay;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.text.DateFormat;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayPlanRespondent implements GroupedRespondent {

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

    //TODO render with proper params
    private void showDayPlan(Context context) {
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        YearMonthDay yearMonthDay = new YearMonthDay(context.queryParamMap(), now.getYear() + MAX_YEAR_OFFSET_VALUE);
        int year = yearMonthDay.year(now.getYear());
        int month = yearMonthDay.month(now.getMonthValue());


        long date = new SmartDate(System.currentTimeMillis()).ofYearMonthSeconds(year, month);
        Map<String, Object> params = new HashMap<>();
        List<Activity> dayActivities = activities.ofUserDate(identity.value(context.req), date);
        params.put(DATE_TEMPLATE_PARAM, dateFormat.format(date));
        params.put(ACTIVITIES_TEMPLATE_PARAM, dayActivities);
        templates.render(context, DAY_PLAN_TEMPLATE, params);
    }
}
