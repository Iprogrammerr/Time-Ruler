package com.iprogrammerr.time.ruler.respondent.activity;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.ActivitiesSearch;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.LimitedDate;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.description.Description;
import com.iprogrammerr.time.ruler.model.description.Descriptions;
import com.iprogrammerr.time.ruler.model.error.ErrorCode;
import com.iprogrammerr.time.ruler.model.error.ResponseException;
import com.iprogrammerr.time.ruler.model.form.ActivityForm;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ActivityRespondent {

    public static final String ACTIVITY = "activity";
    public static final String ID = "id";
    public static final String ACTIVITY_WITH_ID = ACTIVITY + "/:" + ID;
    public static final String ACTIVITY_DONE = ACTIVITY + "/done/:" + ID;
    public static final String ACTIVITY_NOT_DONE = ACTIVITY + "/not-done/:" + ID;
    private final Identity<Long> identity;
    private final ActivityViews views;
    private final DayPlanExecutionRespondent dayPlanExecutionRespondent;
    private final DayPlanRespondent dayPlanRespondent;
    private final Activities activities;
    private final ActivitiesSearch activitiesSearch;
    private final Descriptions descriptions;
    private final LimitedDate limitedDate;
    private final ServerClientDates serverClientDates;

    public ActivityRespondent(Identity<Long> identity, ActivityViews views,
        DayPlanExecutionRespondent dayPlanExecutionRespondent, DayPlanRespondent dayPlanRespondent,
        Activities activities, ActivitiesSearch activitiesSearch, Descriptions descriptions,
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

    public HtmlResponse activityPage(HttpServletRequest request, long templateId, long id, boolean plan) {
        String view;
        if (templateId > 0 && activities.activity(templateId).isPresent()) {
            DescribedActivity activity = descriptions.describedActivity(templateId).withDone(!plan);
            view = views.filled(activity, date -> serverClientDates.zonedClientDate(request, date));
        } else if (id > 0 && activities.activity(id).isPresent()) {
            view = views.filled(descriptions.describedActivity(id),
                date -> serverClientDates.zonedClientDate(request, date));
        } else {
            view = views.empty(serverClientDates.zonedClientDate(request), plan);
        }
        return new HtmlResponse(view);
    }

    public HtmlResponse invalidActivityPage(HttpServletRequest request, String name, String startTime, String endTime,
        String description, boolean plan) {
        return new HtmlResponse(views.withErrors(serverClientDates.zonedClientDate(request), plan,
            new ValidateableName(name), new ValidateableTime(startTime), new ValidateableTime(endTime),
            description));
    }

    public Redirection createActivity(HttpServletRequest request, String date, ActivityForm form) {
        Redirection redirection;
        ValidateableName name = new ValidateableName(form.name, true);
        ValidateableTime start = new ValidateableTime(form.startTime);
        ValidateableTime end = new ValidateableTime(form.endTime);
        if (name.isValid() && start.isValid() && end.isValid()) {
            Instant validatedDate = limitedDate.fromString(date);
            Activity activity = activity(request, validatedDate, name, start, end, form.done);
            long dayStart = new SmartDate(validatedDate)
                .dayBeginningWithOffset(serverClientDates.clientUtcOffset(request));
            createActivity(activity, form.description, activitiesSearch.userDayActivities(identity.value(request),
                dayStart));
            redirection = dayRedirection(request, validatedDate, activity.done);
        } else {
            redirection = errorsRedirection(date, form.name, form.startTime, form.endTime, !form.done);
        }
        return redirection;
    }

    private Redirection dayRedirection(HttpServletRequest request, Instant date, boolean done) {
        Redirection redirection;
        Instant clientNow = serverClientDates.clientDate(request);
        if (new SmartDate(clientNow).isTheSameDay(date)) {
            redirection = dayPlanExecutionRespondent.redirection();
        } else if (done) {
            redirection = dayPlanExecutionRespondent.redirection(date);
        } else {
            redirection = dayPlanRespondent.redirection(date);
        }
        return redirection;
    }

    private Redirection errorsRedirection(String date, String name, String startTime, String endTime,
        boolean plan) {
        return new Redirection(errorsQuery(name, startTime, endTime).put(QueryParams.PLAN, plan)
            .put(QueryParams.DATE, date).build(ACTIVITY));

    }

    private Redirection errorsRedirection(long id, String name, String startTime, String endTime) {
        return new Redirection(errorsQuery(name, startTime, endTime).put(QueryParams.ID, id)
            .build(ACTIVITY));
    }

    private UrlQueryBuilder errorsQuery(String name, String startTime, String endTime) {
        return new UrlQueryBuilder().put(QueryParams.NAME, name).put(QueryParams.START, startTime)
            .put(QueryParams.END, endTime);
    }

    private Activity activity(HttpServletRequest request, Instant clientDate, ValidateableName name,
        ValidateableTime start, ValidateableTime end, boolean done) {
        Instant startTime = start.value();
        Instant endTime = end.value();
        if (startTime.isAfter(endTime)) {
            throw new ResponseException(ErrorCode.GREATER_START_TIME);
        }
        long userId = identity.value(request);
        SmartDate smartDate = new SmartDate(clientDate);
        long startDate = serverClientDates.serverDate(request, smartDate.withTime(startTime)).getEpochSecond();
        long endDate = serverClientDates.serverDate(request, smartDate.withTime(endTime)).getEpochSecond();
        return new Activity(userId, name.value(), startDate, endDate, done);
    }

    private void createActivity(Activity activity, String description, List<Activity> dayActivities) {
        for (Activity da : dayActivities) {
            if (activity.intersects(da)) {
                throw new ResponseException(ErrorCode.ACTIVITIES_INTERSECTS);
            }
        }
        long id = activities.create(activity);
        if (!description.isEmpty()) {
            descriptions.create(new Description(id, description));
        }
    }

    public Redirection updateActivity(HttpServletRequest request, long id, ActivityForm form) {
        Optional<Activity> activity = activities.activity(id);
        if (!activity.isPresent()) {
            throw new ResponseException(ErrorCode.ACTIVITY_NON_EXISTENT_ID);
        }
        if (activity.get().userId != identity.value(request)) {
            throw new ResponseException(ErrorCode.ACTIVITY_NOT_OWNED);
        }
        Redirection redirection;
        ValidateableName name = new ValidateableName(form.name, true);
        ValidateableTime start = new ValidateableTime(form.startTime);
        ValidateableTime end = new ValidateableTime(form.endTime);
        if (name.isValid() && start.isValid() && end.isValid()) {
            Instant date = serverClientDates.clientDate(request, Instant.ofEpochSecond(activity.get().startDate));
            Activity toUpdateActivity = activity(request, date, name, start, end,
                activity.get().done).withId(id);
            long dayStart = new SmartDate(serverClientDates.clientDate(request, toUpdateActivity.startDate))
                .dayBeginningWithOffset(serverClientDates.clientUtcOffset(request));
            updateActivity(toUpdateActivity, form.description,
                activitiesSearch.userDayActivities(identity.value(request), dayStart));
            redirection = dayRedirection(request, date, toUpdateActivity.done);
        } else {
            redirection = errorsRedirection(id, form.name, form.startTime, form.endTime);
        }
        return redirection;
    }

    private void updateActivity(Activity activity, String description, List<Activity> dayActivities) {
        for (Activity da : dayActivities) {
            if (activity.id != da.id && activity.intersects(da)) {
                throw new ResponseException(ErrorCode.ACTIVITIES_INTERSECTS);
            }
        }
        activities.update(activity);
        if (!description.isEmpty()) {
            descriptions.updateOrCreate(new Description(activity.id, description));
        }
    }

    public void deleteActivity(HttpServletRequest request, long id) {
        if (activities.belongsToUser(identity.value(request), id)) {
            activities.delete(id);
        } else {
            throw new ResponseException(ErrorCode.ACTIVITY_NOT_OWNED);
        }
    }

    public void setActivityDone(HttpServletRequest request, long id, boolean done) {
        if (activities.belongsToUser(identity.value(request), id)) {
            activities.setDone(id, done);
        } else {
            throw new ResponseException(ErrorCode.ACTIVITY_NOT_OWNED);
        }
    }
}
