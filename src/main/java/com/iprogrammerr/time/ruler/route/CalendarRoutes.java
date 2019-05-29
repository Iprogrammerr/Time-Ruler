package com.iprogrammerr.time.ruler.route;

import com.iprogrammerr.time.ruler.model.TypedMap;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.CalendarRespondent;
import io.javalin.Context;
import io.javalin.Javalin;

public class CalendarRoutes implements GroupedRoutes {

    private final CalendarRespondent respondent;

    public CalendarRoutes(CalendarRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + CalendarRespondent.PLAN, this::showPlan);
        app.get(group + CalendarRespondent.HISTORY, this::showHistory);
    }

    private void showPlan(Context context) {
        TypedMap params = new TypedMap(context.queryParamMap());
        context.html(respondent.planPage(context.req, params.intValue(QueryParams.YEAR),
            params.intValue(QueryParams.MONTH)).html);
    }

    private void showHistory(Context context) {
        TypedMap params = new TypedMap(context.queryParamMap());
        context.html(respondent.historyPage(context.req, params.intValue(QueryParams.YEAR),
            params.intValue(QueryParams.MONTH)).html);
    }
}
