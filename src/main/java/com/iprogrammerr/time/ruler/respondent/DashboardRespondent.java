package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class DashboardRespondent implements Respondent {

    private static final String DASHBOARD = "dashboard";
    private final Identity<Long> identity;
    private final ViewsTemplates viewsTemplates;

    public DashboardRespondent(Identity<Long> identity, ViewsTemplates viewsTemplates) {
        this.identity = identity;
        this.viewsTemplates = viewsTemplates;
    }

    @Override
    public void init(Javalin app) {
        app.get(DASHBOARD, this::showDashboard);
    }

    private void showDashboard(Context context) {
        try {
            Map<String, Long> params = new HashMap<>();
            params.put("id", identity.value(context.req));
            viewsTemplates.render(context, DASHBOARD, params);
        } catch (Exception e) {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    public void redirect(Context context) {
        context.redirect(DASHBOARD);
    }
}
