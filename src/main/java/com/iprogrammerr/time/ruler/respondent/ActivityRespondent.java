package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.description.Description;
import com.iprogrammerr.time.ruler.model.description.Descriptions;
import com.iprogrammerr.time.ruler.model.session.UtcOffsetAttribute;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import io.javalin.BadRequestResponse;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class ActivityRespondent implements GroupedRespondent {

    private static final String DATE_PARAM = "date";
    private static final String FORM_NAME = "name";
    private static final String FORM_START_TIME = "start";
    private static final String FORM_END_TIME = "end";
    private static final String FORM_DESCRIPTION = "description";
    private static final String FORM_DONE = "done";
    private static final String ACTIVITY = "activity";
    private static final String ID = "id";
    private static final String ACTIVITY_WITH_ID = ACTIVITY + "/:" + ID;
    private final Identity<Long> identity;
    private final ActivityViews views;
    private final DayPlanRespondent dayPlanRespondent;
    private final Days days;
    private final Activities activities;
    private final Descriptions descriptions;
    private final UtcOffsetAttribute offsetAttribute;
    private final LimitedDate limitedDate;

    public ActivityRespondent(Identity<Long> identity, ActivityViews views, DayPlanRespondent dayPlanRespondent, Days days,
        Activities activities, Descriptions descriptions, UtcOffsetAttribute offsetAttribute, LimitedDate limitedDate) {
        this.identity = identity;
        this.views = views;
        this.dayPlanRespondent = dayPlanRespondent;
        this.days = days;
        this.activities = activities;
        this.descriptions = descriptions;
        this.offsetAttribute = offsetAttribute;
        this.limitedDate = limitedDate;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITY, this::showEmpty);
        app.get(group + ACTIVITY_WITH_ID, this::showActivity);
        app.post(group + ACTIVITY, this::createActivity);
        app.post(group + ACTIVITY_WITH_ID, this::saveActivity);
    }

    private void showEmpty(Context context) {
        int utcOffset = offsetAttribute.from(context.req.getSession());
        ZonedDateTime clientDate = ZonedDateTime.now(Clock.systemUTC()).plusSeconds(utcOffset);
        context.html(views.empty(clientDate.getHour(), clientDate.getMinute()));
    }

    private void showActivity(Context context) {
        int id = context.pathParam(ID, Integer.class).get();
        if (activities.exists(id)) {
            context.html(views.filled(descriptions.describedActivity(id)));
        } else {
            showEmpty(context);
        }
    }

    private void createActivity(Context context) {
        ValidateableName name = new ValidateableName(context.formParam(FORM_NAME, ""), true);
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
            dayPlanRespondent.redirect(context, dayDate.toInstant());
        } else {
            context.html(views.withErrors(!name.isValid(), !start.isValid(), !end.isValid()));
        }
    }

    private Day existingOrNew(Context context) {
        long userId = identity.value(context.req);
        long date = limitedDate.fromString(context.queryParam(DATE_PARAM, "")).getEpochSecond();
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
            descriptions.create(new Description(id, description));
        }
    }

    //TODO implementation - will we have id?
    private void saveActivity(Context context) {

    }
}
