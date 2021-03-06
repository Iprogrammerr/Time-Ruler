package com.iprogrammerr.time.ruler.route.day;

import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.day.DayPlanExecutionRespondent;
import com.iprogrammerr.time.ruler.route.GroupedRoutes;
import io.javalin.Javalin;
import io.javalin.core.util.Header;
import io.javalin.http.Context;

public class DayPlanExecutionRoutes implements GroupedRoutes {

    private static final String CACHE_VALUE = "no-store";
    private final DayPlanExecutionRespondent respondent;
    private String group;

    public DayPlanExecutionRoutes(DayPlanExecutionRespondent respondent) {
        this.respondent = respondent;
        this.group = "";
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + DayPlanExecutionRespondent.TODAY, this::showDay);
        app.get(group + DayPlanExecutionRespondent.DAY_PLAN_EXECUTION, this::showDay);
        this.group = group;
    }

    private void showDay(Context context) {
        String date = context.queryParam(QueryParams.DATE, "");
        context.html(respondent.planExecutionPage(context.req, date).html);
        context.header(Header.CACHE_CONTROL, CACHE_VALUE);
    }

    public void redirectToToday(Context context) {
        context.redirect(respondent.redirection().prefixed(group));
    }
}
