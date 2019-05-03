package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.view.rendering.ProfileViews;
import io.javalin.Context;
import io.javalin.Javalin;

public class ProfileRespondent implements GroupedRespondent {

    private static final String PROFILE = "profile";
    private final Identity<Long> identity;
    private final Users users;
    private final ProfileViews views;

    public ProfileRespondent(Identity<Long> identity, Users users, ProfileViews views) {
        this.identity = identity;
        this.users = users;
        this.views = views;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + PROFILE, this::showProfile);
    }

    //TODO render with proper data
    private void showProfile(Context context) {
        context.html(views.view(users.user(identity.value(context.req))));
    }
}
