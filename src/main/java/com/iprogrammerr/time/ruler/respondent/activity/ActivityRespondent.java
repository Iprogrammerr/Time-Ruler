package com.iprogrammerr.time.ruler.respondent.activity;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.description.Description;
import com.iprogrammerr.time.ruler.model.description.Descriptions;
import com.iprogrammerr.time.ruler.respondent.GroupedRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
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
    private static final String PLAN_PARAM = "plan";
    private static final String TEMPLATE_PARAM = "template";
    private static final String FORM_NAME = "name";
    private static final String FORM_START_TIME = "start";
    private static final String FORM_END_TIME = "end";
    private static final String FORM_DESCRIPTION = "description";
    private static final String FORM_DONE = "done";
    private static final String ACTIVITY = "activity";
    private static final String ID = "id";
    private static final String ACTIVITY_WITH_ID = ACTIVITY + "/:" + ID;
    private static final String ACTIVITY_DONE = ACTIVITY + "/done/:" + ID;
    private static final String ACTIVITY_NOT_DONE = ACTIVITY + "/not-done/:" + ID;
    private final Identity<Long> identity;
    private final ActivityViews views;
    private final DayPlanExecutionRespondent dayPlanExecutionRespondent;
    private final DayPlanRespondent dayPlanRespondent;
    private final Activities activities;
    private final ActivitiesSearch activitiesSearch;
    private final Descriptions descriptions;
    private final LimitedDate limitedDate;
    private final ServerClientDates serverClientDates;

    public ActivityRespondent(Identity<Long> identity, ActivityViews views, DayPlanExecutionRespondent dayPlanExecutionRespondent,
        DayPlanRespondent dayPlanRespondent, Activities activities, ActivitiesSearch activitiesSearch, Descriptions descriptions,
        LimitedDate limitedDate, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.dayPlanExecutionRespondent = dayPlanExecutionRespondent;
        this.dayPlanRespondent = dayPlanRespondent;
        this.activities = activities;
        this.activitiesSearch = activitiesSearch;
        this.descriptions = descriptions;
        this.limitedDate = limitedDate;
        this.serverClientDates = serverClientDates;
    }

    @Override
    public void init(String group, Javalin app) {
        String withGroupActivity = group + ACTIVITY;
        String withGroupActivityId = group + ACTIVITY_WITH_ID;
        app.get(withGroupActivity, this::showActivity);
        app.post(withGroupActivity, this::createActivity);
        app.post(withGroupActivityId, this::updateActivity);
        app.put(group + ACTIVITY_DONE, ctx -> setActivityDone(ctx, true));
        app.put(group + ACTIVITY_NOT_DONE, ctx -> setActivityDone(ctx, false));
        app.delete(withGroupActivityId, this::deleteActivity);
    }

    private void showActivity(Context context) {
        long templateId = context.queryParam(TEMPLATE_PARAM, Long.class, Long.toString(0)).get();
        long id = context.queryParam(ID, Long.class, Long.toString(0)).get();
        boolean plan = context.queryParam(PLAN_PARAM, Boolean.class, Boolean.toString(true)).get();
        String view;
        if (templateId > 0 && activities.exists(templateId)) {
            DescribedActivity activity = descriptions.describedActivity(templateId).withDone(!plan);
            view = views.filled(activity, date -> serverClientDates.clientDate(context.req, date));
        } else if (id > 0 && activities.exists(id)) {
            view = views.filled(descriptions.describedActivity(id),
                date -> serverClientDates.clientDate(context.req, date));
        } else {
            view = views.empty(serverClientDates.clientDate(context.req), isActivityPlanned(context));
        }
        context.html(view);
    }

    private boolean isActivityPlanned(Context context) {
        return context.queryParam(PLAN_PARAM, Boolean.class, Boolean.toString(true)).get();
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
                activitiesSearch.ofUserDate(identity.value(context.req), date.getEpochSecond()));
            redirectToDay(context, date, activity.done);
        } else {
            context.html(views.withErrors(isActivityDone(context), name, start, end));
        }
    }

    private void redirectToDay(Context context, Instant date, boolean done) {
        if (done) {
            dayPlanExecutionRespondent.redirect(context, date);
        } else {
            dayPlanRespondent.redirect(context, date);
        }
    }

    private boolean isActivityDone(Context context) {
        return context.formParam(FORM_DONE, Boolean.class).get();
    }

    private Activity activity(Context context, Instant date, ValidateableName name,
        ValidateableTime start, ValidateableTime end) {
        Instant startTime = start.value();
        Instant endTime = end.value();
        if (startTime.isAfter(endTime)) {
            throw new BadRequestResponse("Start time can not be greater than end time");
        }
        long userId = identity.value(context.req);
        boolean done = isActivityDone(context);
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
                activitiesSearch.ofUserDate(identity.value(context.req), date.getEpochSecond()));
            redirectToDay(context, date, activity.done);
        } else {
            context.html(views.withErrors(isActivityDone(context), name, start, end));
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

    private void setActivityDone(Context context, boolean done) {
        long activityId = context.pathParam(ID, Long.class).get();
        if (activities.belongsToUser(identity.value(context.req), activityId)) {
            activities.setDone(activityId, done);
        } else {
            throw new BadRequestResponse("Given activity does not belong to user");
        }
    }
}
