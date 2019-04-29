package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.description.Description;
import com.iprogrammerr.time.ruler.model.description.Descriptions;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import io.javalin.BadRequestResponse;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Instant;
import java.util.List;

//TODO better exception handling/mapping mechanism
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
    private final Activities activities;
    private final Descriptions descriptions;
    private final LimitedDate limitedDate;
    private final ServerClientDates serverClientDates;

    public ActivityRespondent(Identity<Long> identity, ActivityViews views, DayPlanRespondent dayPlanRespondent, Activities activities,
        Descriptions descriptions, LimitedDate limitedDate, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.dayPlanRespondent = dayPlanRespondent;
        this.activities = activities;
        this.descriptions = descriptions;
        this.limitedDate = limitedDate;
        this.serverClientDates = serverClientDates;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ACTIVITY, this::showEmpty);
        app.get(group + ACTIVITY_WITH_ID, this::showActivity);
        app.post(group + ACTIVITY, this::createActivity);
        app.post(group + ACTIVITY_WITH_ID, this::updateActivity);
        app.delete(group + ACTIVITY_WITH_ID, this::deleteActivity);
    }

    private void showEmpty(Context context) {
        context.html(views.empty(serverClientDates.clientDate(context.req)));
    }

    private void showActivity(Context context) {
        int id = context.pathParam(ID, Integer.class).get();
        if (activities.exists(id)) {
            context.html(views.filled(descriptions.describedActivity(id),
                date -> serverClientDates.clientDate(context.req, date)));
        } else {
            showEmpty(context);
        }
    }

    //TODO this ought to be simpler
    private void createActivity(Context context) {
        ValidateableName name = new ValidateableName(context.formParam(FORM_NAME, ""), true);
        ValidateableTime start = new ValidateableTime(context.formParam(FORM_START_TIME, ""));
        ValidateableTime end = new ValidateableTime(context.formParam(FORM_END_TIME, ""));
        String description = context.formParam(FORM_DESCRIPTION, "");
        if (name.isValid() && start.isValid() && end.isValid()) {
            Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
            Activity activity = activity(context, date, name, start, end);
            createActivity(activity, description,
                activities.ofUserDate(identity.value(context.req), date.getEpochSecond()));
            dayPlanRespondent.redirect(context, date);
        } else {
            context.html(views.withErrors(!name.isValid(), !start.isValid(), !end.isValid()));
        }
    }

    private Activity activity(Context context, Instant date, ValidateableName name,
        ValidateableTime start, ValidateableTime end) {
        Instant startTime = start.value();
        Instant endTime = end.value();
        if (startTime.isAfter(endTime)) {
            throw new BadRequestResponse("Start time can not be greater than end time");
        }
        long userId = identity.value(context.req);
        boolean done = context.formParam(FORM_DONE, Boolean.class).get();
        SmartDate smartDate = new SmartDate(date.getEpochSecond());
        long startDate = serverClientDates.serverDate(context.req, smartDate.withTime(startTime)).getEpochSecond();
        long endDate = serverClientDates.serverDate(context.req, smartDate.withTime(endTime)).getEpochSecond();
        return new Activity(userId, name.value(), startDate, endDate, done);
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

    private void updateActivity(Context context) {
        long id = context.pathParam(ID, Integer.class).get();
        if (!activities.exists(id)) {
            throw new BadRequestResponse(String.format("Activity with id = %d does not exist", id));
        }
        ValidateableName name = new ValidateableName(context.formParam(FORM_NAME, ""), true);
        ValidateableTime start = new ValidateableTime(context.formParam(FORM_START_TIME, ""));
        ValidateableTime end = new ValidateableTime(context.formParam(FORM_END_TIME, ""));
        String description = context.formParam(FORM_DESCRIPTION, "");
        if (name.isValid() && start.isValid() && end.isValid()) {
            Instant date = limitedDate.fromString(context.queryParam(DATE_PARAM, ""));
            Activity activity = activity(context, date, name, start, end).withId(id);
            updateActivity(activity, description,
                activities.ofUserDate(identity.value(context.req), date.getEpochSecond()));
            //TODO proper redirect dependent on date value
            dayPlanRespondent.redirect(context, date);
        } else {
            context.html(views.withErrors(!name.isValid(), !start.isValid(), !end.isValid()));
        }
    }

    private void updateActivity(Activity activity, String description, List<Activity> dayActivities) {
        for (Activity da : dayActivities) {
            if (activity.id != da.id && activity.intersects(da)) {
                throw new BadRequestResponse("Activity intersects with existing one");
            }
        }
        activities.update(activity);
        if (!description.isEmpty()) {
            descriptions.updateOrCreate(new Description(activity.id, description));
        }
    }

    private void deleteActivity(Context context) {
        activities.delete(context.pathParam(ID, Integer.class).get());
    }
}
