package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.YearMonthDay;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.BadRequestResponse;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRespondent implements GroupedRespondent {

    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String FORM_NAME = "name";
    private static final String FORM_START_TIME = "start";
    private static final String FORM_END_TIME = "end";
    private static final String FORM_DESCRIPTION = "description";
    private static final String FORM_DONE = "done";
    private static final String ACTIVITY = "activity";
    private static final String ID = "id";
    private final Identity<Long> identity;
    private final ViewsTemplates templates;
    private final DayPlanRespondent dayPlanRespondent;
    private final Days days;
    private final Activities activities;

    public ActivityRespondent(Identity<Long> identity, ViewsTemplates templates, DayPlanRespondent dayPlanRespondent,
        Days days, Activities activities) {
        this.identity = identity;
        this.templates = templates;
        this.dayPlanRespondent = dayPlanRespondent;
        this.days = days;
        this.activities = activities;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITY, this::showEmptyActivity);
        app.get(group + ACTIVITY + "/:id", this::showActivity);
        app.post(group + ACTIVITY, this::createActivity);
        app.post(group + ACTIVITY + "/:id", this::saveActivity);
    }

    private void showEmptyActivity(Context context) {
        templates.render(context, ACTIVITY, new HashMap<>());
    }

    //TODO render with proper params
    private void showActivity(Context context) {
        int id = context.pathParam(ID, Integer.class).get();
        templates.render(context, ACTIVITY, new HashMap<>());
    }

    private void createActivity(Context context) {
        ValidateableName name = new ValidateableName(context.queryParam(FORM_NAME, ""));
        ValidateableTime start = new ValidateableTime(context.queryParam(FORM_START_TIME, ""));
        ValidateableTime end = new ValidateableTime(context.queryParam(FORM_END_TIME, ""));
        String description = context.queryParam(FORM_DESCRIPTION, "");
        //TODO proper values
        if (name.isValid() && start.isValid() && end.isValid()) {
            Instant startTime = start.value();
            Instant endTime = end.value();
            if (startTime.isAfter(endTime)) {
                throw new BadRequestResponse("Start time can not be greater than end time");
            }
            Day day = existingOrNew(context);
            boolean done = context.queryParam(FORM_DONE, Boolean.class, "false").get();
            Activity activity = new Activity(name.value(), day.id, (int) startTime.getEpochSecond(),
                (int) endTime.getEpochSecond(), done);
            createActivity(activity, description, activities.ofUserDate(identity.value(context.req), day.date));
            ZonedDateTime requestedDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(day.date), ZoneOffset.UTC);
            dayPlanRespondent.redirect(context, requestedDate.getYear(), requestedDate.getMonthValue(),
                requestedDate.getDayOfMonth());
        } else {
            //TODO render the same page with errors
            dayPlanRespondent.redirect(context, 0, 0, 0);
        }
    }

    //TODO create or get
    private Day existingOrNew(Context context) {
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        YearMonthDay yearMonthDay = new YearMonthDay(context.queryParamMap(), now.getYear() + MAX_YEAR_OFFSET_VALUE);
        Day day;
        if (days.ofUserExists(identity.value(context.req), now.toEpochSecond())) {

        } else {

        }
        return null;
    }

    private void createActivity(Activity activity, String description, List<Activity> dayActivities) {

    }

    private void printFormParams(Context context) {
        System.out.println("Printing...");
        for (Map.Entry<String, List<String>> entry : context.formParamMap().entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String value : entry.getValue()) {
                System.out.println(value);
            }
        }
    }

    private void saveActivity(Context context) {
        printFormParams(context);
        //TODO proper values
        dayPlanRespondent.redirect(context, 0, 0, 0);
    }
}
