package com.iprogrammerr.time.ruler.respondent.plan;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class DayPlanRespondent implements Respondent {

    private static final String DAY_PLAN_TEMPLATE = "day-plan";
    private static final String DAY_PLAN = "plan/day";
    private static final String OFFSET_PARAM = "offset";
    private static final String DAY_PARAM = "day";
    private final Identity<Long> identity;
    private final ViewsTemplates templates;
    private final Activities activities;

    public DayPlanRespondent(Identity<Long> identity, ViewsTemplates templates, Activities activities) {
        this.identity = identity;
        this.templates = templates;
        this.activities = activities;
    }

    @Override
    public void init(Javalin app) {
        app.get(DAY_PLAN, this::showDayPlan);
    }

    //TODO render with proper data
    public void showDayPlan(Context context) {
        if (identity.isValid(context.req)) {
            renderDayPlan(context);
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    private void renderDayPlan(Context context) {
        Map<String, Object> params = new HashMap<>();
        templates.render(context, DAY_PLAN_TEMPLATE, params);
    }
}
