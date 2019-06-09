package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.PasswordResetViews;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PasswordResetViewsTest {

    private final ViewsTemplates templates = new TestTemplatesSetup().templates();
    private final PasswordResetViews views = new PasswordResetViews(templates);

    @Test
    public void returnsSendEmailView() {
        MatcherAssert.assertThat("Does not return properly rendered send email view", views.sendEmailView(),
            Matchers.equalTo(templates.rendered(views.emailSendName)));
    }

    @Test
    public void returnsEmailSentView() {
        String email = new RandomStrings().email();
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.LINK_SENT, true);
        params.put(TemplatesParams.EMAIL, email);
        MatcherAssert.assertThat("Does not return proper email sent view", views.emailSentView(email),
            Matchers.equalTo(templates.rendered(views.emailSendName, params)));
    }

    @Test
    public void returnsInvalidEmailView() {
        ValidateableEmail invalidEmail = new ValidateableEmail(new RandomStrings().alphanumeric()
            .replace("@", ""));
        returnsInvalidEmailView(invalidEmail);
    }

    private void returnsInvalidEmailView(ValidateableEmail email) {
        Map<String, Object> params = new HashMap<>();
        if (email.isValid()) {
            params.put(TemplatesParams.UNKNOWN_EMAIL, true);
        } else {
            params.put(TemplatesParams.INVALID_EMAIL, true);
        }
        params.put(TemplatesParams.EMAIL, email.value());
        MatcherAssert.assertThat("Does no return invalid email view", views.invalidEmailView(email),
            Matchers.equalTo(templates.rendered(views.emailSendName, params)));
    }

    @Test
    public void returnsUnknownEmailView() {
        returnsInvalidEmailView(new ValidateableEmail(new RandomStrings().email()));
    }

    @Test
    public void returnsInactiveAccountView() {
        String email = new RandomStrings().email();
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, email);
        params.put(TemplatesParams.INACTIVE_ACCOUNT, true);
        MatcherAssert.assertThat("Does not return inactive account view", views.inactiveAccountView(email),
            Matchers.equalTo(templates.rendered(views.emailSendName, params)));
    }

    @Test
    public void returnsChangePasswordView() {
        returnsChangePasswordView(false);
    }

    private void returnsChangePasswordView(boolean invalidPassword) {
        String passwordResetUrl = new RandomStrings().alphanumeric();
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.PASSWORD_RESET_URL, passwordResetUrl);
        params.put(TemplatesParams.INVALID_PASSWORD, invalidPassword);
        MatcherAssert.assertThat("Does not return change password view",
            views.changePasswordView(passwordResetUrl, invalidPassword),
            Matchers.equalTo(templates.rendered(views.changePasswordName, params)));
    }

    @Test
    public void returnsInvalidChangePasswordView() {
        returnsChangePasswordView(true);
    }
}
