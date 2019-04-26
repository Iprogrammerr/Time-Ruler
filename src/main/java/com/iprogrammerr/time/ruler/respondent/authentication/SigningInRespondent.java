package com.iprogrammerr.time.ruler.respondent.authentication;

import com.iprogrammerr.time.ruler.model.Hashing;
import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.session.UtcOffsetAttribute;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.model.user.Users;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.respondent.TodayRespondent;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;
import com.iprogrammerr.time.ruler.view.rendering.SigningInView;
import io.javalin.Context;
import io.javalin.Javalin;

import java.util.List;

public class SigningInRespondent implements Respondent {

    private static final String SIGN_IN = "sign-in";
    private static final String FORM_EMAIL_LOGIN = "emailLogin";
    private static final String FORM_PASSWORD = "password";
    private static final String FORM_UTC_OFFSET = "utcOffset";
    private static final String ACTIVATION = "activation";
    private final TodayRespondent respondent;
    private final SigningInView signInView;
    private final Users users;
    private final Hashing hashing;
    private final Identity<Long> identity;
    private final UtcOffsetAttribute offsetAttribute;

    public SigningInRespondent(TodayRespondent respondent, SigningInView signInView, Users users, Hashing hashing,
        Identity<Long> identity, UtcOffsetAttribute offsetAttribute) {
        this.respondent = respondent;
        this.signInView = signInView;
        this.users = users;
        this.hashing = hashing;
        this.identity = identity;
        this.offsetAttribute = offsetAttribute;
    }

    @Override
    public void init(Javalin app) {
        app.get(SIGN_IN, this::renderSignIn);
        app.post(SIGN_IN, this::signIn);
    }

    private void renderSignIn(Context context) {
        String activation = context.queryParam(ACTIVATION, "");
        if (activation.isEmpty()) {
            if (context.req.getSession(false) == null) {
                context.html(signInView.valid());
            } else {
                respondent.redirect(context);
            }
        } else {
            activate(context, activation);
        }
    }

    public void signIn(Context context) {
        String emailOrLogin = context.formParam(FORM_EMAIL_LOGIN, "");
        ValidateableEmail email = new ValidateableEmail(emailOrLogin);
        ValidateableName name = new ValidateableName(emailOrLogin);
        ValidateablePassword password = new ValidateablePassword(context.formParam(FORM_PASSWORD, ""));
        if ((email.isValid() || name.isValid()) && password.isValid()) {
            signInOrSetError(context, email.isValid() ? email.value() : name.value(), hashing.hash(password.value()));
        } else {
            context.html(signInView.invalid(emailOrLogin, password.isValid()));
        }
    }

    private void signInOrSetError(Context context, String emailOrName, String passwordHash) {
        if (users.existsWithEmailOrName(emailOrName)) {
            User user = users.byEmailOrName(emailOrName);
            if (passwordHash.equals(user.password)) {
                identity.create(user.id, context.req);
                int utcOffset = context.formParam(FORM_UTC_OFFSET, Integer.class).get();
                offsetAttribute.to(context.req.getSession(), utcOffset);
                respondent.redirect(context);
            } else {
                context.html(signInView.invalid(emailOrName, true));
            }
        } else {
            context.html(signInView.invalid(emailOrName, false));
        }
    }

    //TODO simplify update, handle exceptions
    private void activate(Context context, String activation) {
        List<User> inactive = users.allInactive();
        boolean activated = false;
        for (User u : inactive) {
            String hash = userHash(u.email, u.name, u.id);
            if (activation.equals(hash)) {
                users.update(new User(u.id, u.name, u.email, u.password, true));
                activated = true;
                break;
            }
        }
        if (activated) {
            context.html(signInView.withActivationCongratulations());
        } else {
            throw new RuntimeException("Given activation link is invalid");
        }
    }

    private String userHash(String email, String name, long id) {
        return hashing.hash(email, name, String.valueOf(id));
    }
}