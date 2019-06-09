package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.TemplatesParams;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SigningUpViewsTest {

    private final ViewsTemplates templates = new TestTemplatesSetup().templates();
    private final RandomStrings strings = new RandomStrings();
    private final SigningUpViews views = new SigningUpViews(templates, strings.alphanumeric());

    @Test
    public void returnsView() {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.SIGN_IN_URL, views.signInUrl);
        MatcherAssert.assertThat("Does not return proper view", views.view(),
            Matchers.equalTo(templates.rendered(views.formName, params)));
    }

    @Test
    public void returnsInvalidEmailView() {
        returnsInvalidView(new ValidateableEmail(strings.random().replace("@", "")),
            new ValidateableName(strings.name()), false);
    }

    private void returnsInvalidView(ValidateableEmail email, ValidateableName name, boolean invalidPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.INVALID_EMAIL, !email.isValid());
        params.put(TemplatesParams.EMAIL, email.value());
        params.put(TemplatesParams.INVALID_NAME, !name.isValid());
        params.put(TemplatesParams.NAME, name.value());
        params.put(TemplatesParams.INVALID_PASSWORD, invalidPassword);
        params.put(TemplatesParams.SIGN_IN_URL, views.signInUrl);
        MatcherAssert.assertThat("Does not return invalid view", views.invalidView(email, name, invalidPassword),
            Matchers.equalTo(templates.rendered(views.formName, params)));
    }

    @Test
    public void returnsInvalidNameView() {
        returnsInvalidView(new ValidateableEmail(strings.email()), new ValidateableName(strings.nonAlphabetic()),
            false);
    }

    @Test
    public void returnsInvalidPasswordView() {
        returnsInvalidView(new ValidateableEmail(strings.email()), new ValidateableName(strings.name()), true);
    }

    @Test
    public void returnsTakenEmailView() {
        returnsTakenView(true, false);
    }

    private void returnsTakenView(boolean emailTaken, boolean nameTaken) {
        String email = strings.email();
        String name = strings.name();
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, email);
        params.put(TemplatesParams.NAME, name);
        params.put(TemplatesParams.EMAIL_TAKEN, emailTaken);
        params.put(TemplatesParams.NAME_TAKEN, nameTaken);
        params.put(TemplatesParams.SIGN_IN_URL, views.signInUrl);
        MatcherAssert.assertThat("Does not return taken view", views.takenView(email, name, emailTaken, nameTaken),
            Matchers.equalTo(templates.rendered(views.formName, params)));
    }

    @Test
    public void returnsTakenNameView() {
        returnsTakenView(false, true);
    }

    @Test
    public void returnsSuccessView() {
        MatcherAssert.assertThat("Does not return success view", views.successView(),
            Matchers.equalTo(templates.rendered(views.successName)));
    }
}
