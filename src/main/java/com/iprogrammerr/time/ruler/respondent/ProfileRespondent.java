package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class ProfileRespondent implements Respondent {

    private static final String PROFILE = "profile";
    private final Identity<Long> identity;
    private final ViewsTemplates viewsTemplates;

    public ProfileRespondent(Identity<Long> identity, ViewsTemplates viewsTemplates) {
        this.identity = identity;
        this.viewsTemplates = viewsTemplates;
    }

    @Override
    public void init(Javalin app) {
        app.get(PROFILE, this::showMainPage);
    }

    //TODO render with proper data
    private void showMainPage(Context context) {
        if (identity.isValid(context.req)) {
            viewsTemplates.render(context, PROFILE, new HashMap<>());
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }
}
