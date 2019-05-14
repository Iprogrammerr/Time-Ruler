package com.iprogrammerr.time.ruler.route;

import com.iprogrammerr.time.ruler.model.form.FormParams;
import com.iprogrammerr.time.ruler.model.param.ProfileParams;
import com.iprogrammerr.time.ruler.respondent.ProfileRespondent;
import com.iprogrammerr.time.ruler.respondent.Redirection;
import io.javalin.Context;
import io.javalin.Javalin;

public class ProfileRoutes implements GroupedRoutes {

    private final ProfileRespondent respondent;
    private String prefix;

    public ProfileRoutes(ProfileRespondent respondent) {
        this.respondent = respondent;
        this.prefix = "";
    }

    @Override
    public void init(String group, Javalin app) {
        prefix = group;
        app.get(group + ProfileRespondent.PROFILE, this::showProfile);
        app.post(group + ProfileRespondent.PROFILE_EMAIL, this::updateEmail);
        app.post(group + ProfileRespondent.PROFILE_NAME, this::updateName);
        app.post(group + ProfileRespondent.PROFILE_PASSWORD, this::updatePassword);
    }

    private void showProfile(Context context) {
        context.html(respondent.profilePage(context.req, ProfileParams.fromQuery(context.queryParamMap())).html);
    }

    private void updateEmail(Context context) {
        Redirection redirection = respondent.updateEmail(context.req, context.formParam(FormParams.EMAIL));
        context.redirect(redirection.prefixed(prefix));
    }

    private void updateName(Context context) {
        Redirection redirection = respondent.updateName(context.req, context.formParam(FormParams.NAME));
        context.redirect(redirection.prefixed(prefix));
    }

    private void updatePassword(Context context) {
        Redirection redirection = respondent.updatePassword(context.req, context.formParam(FormParams.OLD_PASSWORD),
            context.formParam(FormParams.NEW_PASSWORD));
        context.redirect(redirection.prefixed(prefix));
    }
}

