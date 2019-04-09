package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class ProfileRespondent implements Respondent {

    private static final String PROFILE = "profile";
    private static final String EMAIL_TEMPLATE = "email";
    private static final String NAME_TEMPLATE = "name";
    private final Identity<Long> identity;
    private final Users users;
    private final ViewsTemplates viewsTemplates;

    public ProfileRespondent(Identity<Long> identity, Users users, ViewsTemplates viewsTemplates) {
        this.identity = identity;
        this.users = users;
        this.viewsTemplates = viewsTemplates;
    }

    @Override
    public void init(Javalin app) {
        app.get(PROFILE, this::showMainPage);
    }

    //TODO render with proper data
    private void showMainPage(Context context) {
        if (identity.isValid(context.req)) {
            User user = users.user(identity.value(context.req));
            Map<String, String> params = new HashMap<>();
            params.put(EMAIL_TEMPLATE, user.email);
            params.put(NAME_TEMPLATE, user.name);
            viewsTemplates.render(context, PROFILE, params);
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }
}
