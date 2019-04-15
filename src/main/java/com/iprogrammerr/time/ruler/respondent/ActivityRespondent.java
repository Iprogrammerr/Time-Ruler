package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class ActivityRespondent implements Respondent {

    private static final String ACTIVITY = "activity";
    private final Identity<Long> identity;
    private final ViewsTemplates templates;

    public ActivityRespondent(Identity<Long> identity, ViewsTemplates templates) {
        this.identity = identity;
        this.templates = templates;
    }

    @Override
    public void init(Javalin app) {
        app.get(ACTIVITY, this::showActivityPage);
    }

    private void showActivityPage(Context context) {
        if (identity.isValid(context.req)) {
            renderActivityPage(context);
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    //TODO render with proper params
    private void renderActivityPage(Context context) {
        templates.render(context, ACTIVITY, new HashMap<>());
    }
}
