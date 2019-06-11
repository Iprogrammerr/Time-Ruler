package com.iprogrammerr.time.ruler.route.activity;

import com.iprogrammerr.time.ruler.model.TypedMap;
import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.activity.ActivitiesRespondent;
import com.iprogrammerr.time.ruler.route.GroupedRoutes;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class ActivitiesRoutes implements GroupedRoutes {

    private final ActivitiesRespondent respondent;

    public ActivitiesRoutes(ActivitiesRespondent respondent) {
        this.respondent = respondent;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + ActivitiesRespondent.ACTIVITIES, this::showPage);
    }

    private void showPage(Context context) {
        TypedMap params = new TypedMap(context.queryParamMap());
        HtmlResponse response = respondent.page(context.req, params.intValue(QueryParams.PAGE, 1),
            params.booleanValue(QueryParams.PLAN), params.stringValue(QueryParams.PATTERN),
            context.queryParamMap());
        context.html(response.html);
    }
}
