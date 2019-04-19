package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class TodayRespondent implements GroupedRespondent {

    private static final String TODAY = "today";
    private final Identity<Long> identity;
    private final ViewsTemplates viewsTemplates;
    private String groupedToday;

    public TodayRespondent(Identity<Long> identity, ViewsTemplates viewsTemplates) {
        this.identity = identity;
        this.viewsTemplates = viewsTemplates;
        this.groupedToday = TODAY;
    }

    @Override
    public void init(String group, Javalin app) {
        groupedToday = group + TODAY;
        app.get(groupedToday, this::showMainPage);
    }

    //TODO render with proper data
    private void showMainPage(Context context) {
        if (identity.isValid(context.req)) {
            viewsTemplates.render(context, TODAY, new HashMap<>());
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    public void redirect(Context context) {
        context.redirect(groupedToday);
    }
}
