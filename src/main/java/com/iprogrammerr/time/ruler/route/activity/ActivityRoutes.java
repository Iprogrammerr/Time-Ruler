package com.iprogrammerr.time.ruler.route.activity;

import com.iprogrammerr.time.ruler.model.TypedMap;
import com.iprogrammerr.time.ruler.model.form.ActivityForm;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.activity.ActivityRespondent;
import com.iprogrammerr.time.ruler.route.GroupedRoutes;
import io.javalin.Context;
import io.javalin.Javalin;

public class ActivityRoutes implements GroupedRoutes {

    private static final long EMPTY_ID = -1;
    private final ActivityRespondent respondent;
    private String prefix;

    public ActivityRoutes(ActivityRespondent respondent) {
        this.respondent = respondent;
        this.prefix = "";
    }

    @Override
    public void init(String group, Javalin app) {
        prefix = group;
        String withGroupActivity = group + ActivityRespondent.ACTIVITY;
        String withGroupActivityId = group + ActivityRespondent.ACTIVITY_WITH_ID;
        app.get(withGroupActivity, this::showActivity);
        app.post(withGroupActivity, this::createActivity);
        app.post(withGroupActivityId, this::updateActivity);
        app.put(group + ActivityRespondent.ACTIVITY_DONE, ctx -> setActivityDone(ctx, true));
        app.put(group + ActivityRespondent.ACTIVITY_NOT_DONE, ctx -> setActivityDone(ctx, false));
        app.delete(withGroupActivityId, this::deleteActivity);
    }

    private void showActivity(Context context) {
        TypedMap params = new TypedMap(context.queryParamMap());
        String name = params.stringValue(QueryParams.NAME);
        String startTime = params.stringValue(QueryParams.START);
        String endTime = params.stringValue(QueryParams.END);
        String description = params.stringValue(QueryParams.DESCRIPTION);
        boolean plan = params.booleanValue(QueryParams.PLAN);
        HtmlResponse response;
        if (name.isEmpty() && startTime.isEmpty() && endTime.isEmpty()) {
            response = respondent.activityPage(context.req, params.longValue(QueryParams.TEMPLATE, EMPTY_ID),
                params.longValue(QueryParams.ID, EMPTY_ID), plan);
        } else {
            response = respondent.invalidActivityPage(context.req, name, startTime, endTime, description, plan);
        }
        context.html(response.html);
    }

    private void createActivity(Context context) {
        String date = context.queryParam(QueryParams.DATE, "");
        context.redirect(respondent.createActivity(context.req, date,
            ActivityForm.parsed(context.formParamMap())).prefixed(prefix));
    }

    private void updateActivity(Context context) {
        long id = context.pathParam(ActivityRespondent.ID, Long.class).get();
        ActivityForm form = ActivityForm.parsed(context.formParamMap());
        context.redirect(respondent.updateActivity(context.req, id, form).prefixed(prefix));
    }

    private void setActivityDone(Context context, boolean done) {
        respondent.setActivityDone(context.req, context.pathParam(ActivityRespondent.ID, Long.class).get(),
            done);
    }

    private void deleteActivity(Context context) {
        respondent.deleteActivity(context.req, context.pathParam(ActivityRespondent.ID, Long.class).get());
    }
}
