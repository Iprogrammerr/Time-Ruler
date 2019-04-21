package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.SmartDate;
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

    //TODO messages layer
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String FORM_NAME = "name";
    private static final String FORM_START_TIME = "start";
    private static final String FORM_END_TIME = "end";
    private static final String FORM_DESCRIPTION = "description";
    private static final String FORM_DONE = "done";
    private static final String ACTIVITY = "activity";
    private static final String ID = "id";
    private static final String ACTIVITY_WITH_ID = ACTIVITY + "/:" + ID;
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
        app.get(group + ACTIVITY_WITH_ID, this::showActivity);
        app.post(group + ACTIVITY, this::createActivity);
        app.post(group + ACTIVITY_WITH_ID, this::saveActivity);
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
        ValidateableName name = new ValidateableName(context.formParam(FORM_NAME, ""));
        ValidateableTime start = new ValidateableTime(context.formParam(FORM_START_TIME, ""));
        ValidateableTime end = new ValidateableTime(context.formParam(FORM_END_TIME, ""));
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
            ZonedDateTime dayDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(day.date), ZoneOffset.UTC);
            dayPlanRespondent.redirect(context, dayDate.getYear(), dayDate.getMonthValue(),
                dayDate.getDayOfMonth());
        } else {
            //TODO proper messages
            Map<String, String> errors = new HashMap<>();
            if (!name.isValid()) {
                errors.put(INVALID_NAME_TEMPLATE, "Invalid name");
            }
            if (!start.isValid()) {
                errors.put(INVALID_START_TIME_TEMPLATE, "Invalid time format, expected: HH:MM");
            }
            if (!end.isValid()) {
                errors.put(INVALID_END_TIME_TEMPLATE, "Invalid time format, expected: HH:MM");
            }
            templates.render(context, ACTIVITY, errors);

        }
    }

    private Day existingOrNew(Context context) {
        ZonedDateTime now = ZonedDateTime.now(Clock.systemUTC());
        YearMonthDay yearMonthDay = new YearMonthDay(context.queryParamMap(), now.getYear() + MAX_YEAR_OFFSET_VALUE);
        long userId = identity.value(context.req);
        long date = new SmartDate(now)
            .ofYearMonthDay(yearMonthDay.year(now.getYear()), yearMonthDay.month(now.getMonthValue()),
                yearMonthDay.day(now.getDayOfMonth()))
            .toEpochSecond();
        Day day;
        if (days.ofUserExists(userId, date)) {
            day = days.ofUser(userId, date);
        } else {
            day = new Day(days.createForUser(userId, date), userId, date);
        }
        return day;
    }

    private void createActivity(Activity activity, String description, List<Activity> dayActivities) {
        for (Activity da : dayActivities) {
            if (activity.intersects(da)) {
                throw new BadRequestResponse("New activity intersects with existing one");
            }
        }
        long id = activities.create(activity);
        if (!description.isEmpty()) {
            //TODO create description
        }
    }

    //TODO implementation - will we have id?
    private void saveActivity(Context context) {

    }
}
