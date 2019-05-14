package com.iprogrammerr.time.ruler.route.activity;

import com.iprogrammerr.time.ruler.model.param.QueryParams;
import com.iprogrammerr.time.ruler.model.RequestParams;
import com.iprogrammerr.time.ruler.respondent.HtmlResponse;
import com.iprogrammerr.time.ruler.respondent.activity.ActivitiesRespondent;
import com.iprogrammerr.time.ruler.route.GroupedRoutes;
import io.javalin.Context;
import io.javalin.Javalin;

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
        RequestParams params = new RequestParams(context);
        HtmlResponse response = respondent.page(context.req, params.intParam(QueryParams.PAGE, 1),
            params.booleanParam(QueryParams.PLAN), params.stringParam(QueryParams.PATTERN),
            context.queryParamMap());
        context.html(response.html);
    }
}
