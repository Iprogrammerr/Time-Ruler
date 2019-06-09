package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import com.iprogrammerr.time.ruler.view.rendering.SigningInViews;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SigningInViewsTest {

    private static final String AT = "@";
    private final ViewsTemplates templates = new TestTemplatesSetup().templates();
    private final RandomStrings strings = new RandomStrings();
    private final SigningInViews views = new SigningInViews(templates, strings.random(), strings.random());

    @Test
    public void returnsValidView() {
        Map<String, Object> params = new HashMap<>();
        addUrls(params);
        MatcherAssert.assertThat("Does not return valid view", views.validView(),
            Matchers.equalTo(templates.rendered(views.name, params)));
    }

    private void addUrls(Map<String, Object> params) {
        params.put(TemplatesParams.SIGN_UP_URL, views.signUpUrl);
        params.put(TemplatesParams.PASSWORD_RESET_URL, views.passwordResetUrl);
    }

    @Test
    public void returnsInvalidNameView() {
        returnsInvalidView(strings.nonAlphabetic().replace(AT, ""), false, false, false,
            false);
    }

    private void returnsInvalidView(String emailName, boolean nonExistentUser, boolean inactiveAccount,
        boolean notUserPassword, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        boolean invalidEmailName;
        if (emailName.contains(AT)) {
            invalidEmailName = !new ValidateableEmail(emailName).isValid();
        } else {
            invalidEmailName = !new ValidateableName(emailName).isValid();
        }
        params.put(TemplatesParams.INVALID_EMAIL_NAME, invalidEmailName);
        params.put(TemplatesParams.EMAIL_NAME, emailName);
        params.put(TemplatesParams.NON_EXISTENT_USER, nonExistentUser);
        params.put(TemplatesParams.INACTIVE_ACCOUNT, inactiveAccount);
        params.put(TemplatesParams.NOT_USER_PASSWORD, notUserPassword);
        params.put(TemplatesParams.INVALID_PASSWORD, invalidPassword);
        addUrls(params);
        MatcherAssert.assertThat("Does not return invalid view",
            views.invalidView(emailName, nonExistentUser, inactiveAccount, notUserPassword, invalidPassword),
            Matchers.equalTo(templates.rendered(views.name, params)));
    }

    @Test
    public void returnsInvalidEmailView() {
        returnsInvalidView(AT + strings.random(), false, false, false, false);
    }

    @Test
    public void returnsNonExistentUserView() {
        returnsInvalidView(strings.name(), true, false, false, false);
    }

    @Test
    public void returnsInactiveAccountView() {
        returnsInvalidView(strings.name(), false, true, false, false);
    }

    @Test
    public void returnsNotUserPasswordView() {
        returnsInvalidView(strings.email(), false, false, true, false);
    }

    @Test
    public void returnsInvalidPasswordView() {
        returnsInvalidView(strings.email(), false, false, false, true);
    }

    @Test
    public void returnsWithNewPasswordView() {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.PASSWORD_CHANGE, true);
        addUrls(params);
        MatcherAssert.assertThat("Does not return with new password view", views.withNewPasswordView(),
            Matchers.equalTo(templates.rendered(views.name, params)));
    }

    @Test
    public void returnsWithFarewellView() {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.SIGN_OUT, true);
        addUrls(params);
        MatcherAssert.assertThat("Does not return with farewell view", views.withFarewellView(),
            Matchers.equalTo(templates.rendered(views.name, params)));
    }

    @Test
    public void returnsWithActivationCongratulationsView() {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.ACTIVATION, true);
        addUrls(params);
        MatcherAssert.assertThat("Does not return with activation congratulations view",
            views.withActivationCongratulationsView(), Matchers.equalTo(templates.rendered(views.name, params)));
    }
}
