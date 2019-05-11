package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.email.EmailServer;
import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.UrlQueryBuilder;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.model.user.UsersActualization;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.view.rendering.PasswordResetViews;
import io.javalin.Context;
import io.javalin.Javalin;

public class PasswordResetRespondent implements Respondent {

    public static final String PASSWORD_RESET = "password-reset";
    private static final String EMAIL_PARAM = "email";
    private static final String EMAIL_SENT_PARAM = "emailSent";
    private final Users users;
    private final UsersActualization actualization;
    private final EmailServer server;
    private final Hashing hashing;
    private final PasswordResetViews views;

    public PasswordResetRespondent(Users users, UsersActualization actualization, EmailServer server, Hashing hashing,
        PasswordResetViews views) {
        this.users = users;
        this.actualization = actualization;
        this.server = server;
        this.hashing = hashing;
        this.views = views;
    }

    @Override
    public void init(Javalin app) {
        app.get(PASSWORD_RESET, this::showPasswordReset);
        app.post(PASSWORD_RESET, this::sentPasswordResetEmail);
    }

    private void showPasswordReset(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.queryParam(EMAIL_PARAM, ""));
        boolean emailSent = context.queryParam(EMAIL_SENT_PARAM, Boolean.class, Boolean.toString(false)).get();
        if (emailSent && email.isValid()) {
            context.html(views.emailSentView(email.value()));
        } else if (shouldShowEmptyPasswordReset(email)) {
            context.html(views.view());
        } else {
            context.html(views.invalidEmailView(email));
        }
    }

    private boolean shouldShowEmptyPasswordReset(ValidateableEmail email) {
        return email.value().isEmpty() || (email.isValid() && users.withEmail(email.value()).isPresent());
    }

    private void sentPasswordResetEmail(Context context) {
        ValidateableEmail email = new ValidateableEmail(context.formParam(EMAIL_PARAM, ""));
        if (email.isValid() && users.withEmail(email.value()).isPresent()) {
            //TODO sent email
            redirect(context, email, true);
        } else {
            redirect(context, email, false);
        }
    }

    private void redirect(Context context, ValidateableEmail email, boolean emailSent) {
        context.redirect(new UrlQueryBuilder().put(EMAIL_PARAM, email.value())
            .put(EMAIL_SENT_PARAM, emailSent).build());
    }
}
