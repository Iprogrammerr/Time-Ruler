package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.YearMonthDay;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.session.UtcOffsetAttribute;
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
    private static final String INVALID_NAME_TEMPLATE = "invalidName";
    private static final String INVALID_START_TIME_TEMPLATE = "invalidStartTime";
    private static final String INVALID_END_TIME_TEMPLATE = "invalidEndTime";
    private static final String START_TIME_TEMPLATE = "start";
    private static final String END_TIME_TEMPLATE = "end";
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
    private final UtcOffsetAttribute offsetAttribute;

    public ActivityRespondent(Identity<Long> identity, ViewsTemplates templates, DayPlanRespondent dayPlanRespondent,
        Days days, Activities activities, UtcOffsetAttribute offsetAttribute) {
        this.identity = identity;
        this.templates = templates;
        this.dayPlanRespondent = dayPlanRespondent;
        this.days = days;
        this.activities = activities;
        this.offsetAttribute = offsetAttribute;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITY, this::showEmptyActivity);
        app.get(group + ACTIVITY_WITH_ID, this::showActivity);
        app.post(group + ACTIVITY, this::createActivity);
        app.post(group + ACTIVITY_WITH_ID, this::saveActivity);
    }

    private void showEmptyActivity(Context context) {
        int utcOffset = offsetAttribute.from(context.req.getSession());
        ZonedDateTime clientDate = ZonedDateTime.now(Clock.systemUTC()).plusSeconds(utcOffset);
        String time = String.format("%02d:%02d", clientDate.getHour(), clientDate.getMinute());
        Map<String, String> params = new HashMap<>();
        params.put(START_TIME_TEMPLATE, time);
        params.put(END_TIME_TEMPLATE, time);
        templates.render(context, ACTIVITY, params);
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
            renderActivityWithErrors(context, name.isValid(), start.isValid(), end.isValid());
        }
    }

    private void renderActivityWithErrors(Context context, boolean nameValid, boolean startValid, boolean endValid) {
        Map<String, Object> errors = new HashMap<>();
        errors.put(INVALID_NAME_TEMPLATE, !nameValid);
        errors.put(INVALID_START_TIME_TEMPLATE, !startValid);
        errors.put(INVALID_END_TIME_TEMPLATE, !endValid);
        templates.render(context, ACTIVITY, errors);
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
