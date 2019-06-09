package com.iprogrammerr.time.ruler.view;

import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.user.User;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.tool.RandomUsers;
import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.view.rendering.ProfileViews;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewsTest {

    private final ViewsTemplates templates = new TestTemplatesSetup().templates();
    private final ProfileViews views = new ProfileViews(templates);

    @Test
    public void returnsView() {
        User user = new RandomUsers().user();
        MatcherAssert.assertThat("Does not return profile view", view(user, false,
            false), Matchers.equalTo(views.view(user)));
    }

    private String view(User user, boolean emailChanged, boolean nameChanged) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, user.email);
        params.put(TemplatesParams.NAME, user.name);
        params.put(TemplatesParams.EMAIL_CHANGED, emailChanged);
        params.put(TemplatesParams.NAME_CHANGED, nameChanged);
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.PROFILE);
        return templates.rendered(views.name, params);
    }

    @Test
    public void returnsEmailChangedView() {
        User user = new RandomUsers().user();
        MatcherAssert.assertThat("Does not return email changed view", view(user, true,
            false), Matchers.equalTo(views.emailChangedView(user)));
    }

    @Test
    public void returnsNameChangedView() {
        User user = new RandomUsers().user();
        MatcherAssert.assertThat("Does not return name changed view", view(user, false,
            true), Matchers.equalTo(views.nameChangedView(user)));
    }

    @Test
    public void returnsInvalidEmailView() {
        RandomStrings strings = new RandomStrings();
        ValidateableEmail invalidEmail = new ValidateableEmail(strings.alphanumeric()
            .replace("@", ""));
        returnsInvalidEmailView(invalidEmail, false, strings.name());
    }

    private void returnsInvalidEmailView(ValidateableEmail email, boolean usedEmail, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, email.value());
        params.put(TemplatesParams.INVALID_EMAIL, !email.isValid());
        params.put(TemplatesParams.USED_EMAIL, usedEmail);
        params.put(TemplatesParams.NAME, name);
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.PROFILE);
        MatcherAssert.assertThat("Does not return invalid email view", views.invalidEmailView(email,
            usedEmail, name), Matchers.equalTo(templates.rendered(views.name, params)));
    }

    @Test
    public void returnsUsedEmailView() {
        RandomStrings strings = new RandomStrings();
        returnsInvalidEmailView(new ValidateableEmail(strings.email()), true, strings.name());
    }

    @Test
    public void returnsInvalidNameView() {
        RandomStrings strings = new RandomStrings();
        returnsInvalidNameView(new ValidateableName(strings.random()), false, strings.email());
    }

    private void returnsInvalidNameView(ValidateableName name, boolean usedName, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.NAME, name.value());
        params.put(TemplatesParams.INVALID_NAME, !name.isValid());
        params.put(TemplatesParams.USED_NAME, usedName);
        params.put(TemplatesParams.EMAIL, email);
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.PROFILE);
        MatcherAssert.assertThat("Does not return invalid name view", views.invalidNameView(name, usedName,
            email), Matchers.equalTo(templates.rendered(views.name, params)));
    }

    @Test
    public void returnsUsedNameView() {
        RandomStrings strings = new RandomStrings();
        returnsInvalidNameView(new ValidateableName(strings.name()), true, strings.email());
    }

    @Test
    public void returnsInvalidOldPasswordView() {
        returnsInvalidPasswordView(new RandomUsers().user(), true, false, false);
    }

    private void returnsInvalidPasswordView(User user, boolean invalidOldPassword, boolean notUserPassword,
        boolean invalidNewPassword) {
        Map<String, Object> params = new HashMap<>();
        params.put(TemplatesParams.EMAIL, user.email);
        params.put(TemplatesParams.NAME, user.name);
        params.put(TemplatesParams.SHOW_PASSWORDS, true);
        params.put(TemplatesParams.INVALID_OLD_PASSWORD, invalidOldPassword);
        params.put(TemplatesParams.NOT_USER_PASSWORD, notUserPassword);
        params.put(TemplatesParams.INVALID_NEW_PASSWORD, invalidNewPassword);
        params.put(TemplatesParams.ACTIVE_TAB, ActiveTab.PROFILE);
        MatcherAssert.assertThat("Returns invalid password view", views.invalidPasswordView(user, invalidOldPassword,
            notUserPassword, invalidNewPassword), Matchers.equalTo(templates.rendered(views.name, params)));
    }

    @Test
    public void returnsNotUserPasswordView() {
        returnsInvalidPasswordView(new RandomUsers().user(), false, true, false);
    }

    @Test
    public void returnsInvalidNewPasswordView() {
        returnsInvalidPasswordView(new RandomUsers().user(), false, false, true);
    }
}
