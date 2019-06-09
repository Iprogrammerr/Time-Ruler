package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class ConfigurationTest {

    private Properties properties;

    @Before
    public void setup() {
        properties = new Properties();
        properties.put(Configuration.PORT, "8080");
        properties.put(Configuration.DATABASE_USER, "time_ruler");
        properties.put(Configuration.DATABASE_PASSWORD, "time_ruler");
        properties.put(Configuration.JDBC_URL, "jdbc:h2:mem:time_ruler");
        properties.put(Configuration.ADMIN_EMAIL, "a68c02e9232d83");
        properties.put(Configuration.ADMIN_PASSWORD, "fd38406ed994ff");
        properties.put(Configuration.SMTP_HOST, "smtp.mailtrap.io");
        properties.put(Configuration.SMTP_PORT, "25");
        properties.put(Configuration.EMAILS_LINKS_BASE, "http://127.0.0.1:8080/");
        properties.put(Configuration.SIGN_UP_EMAIL_SUBJECT, "Welcome to Time Ruler");
        properties.put(Configuration.SIGN_UP_EMAIL_TEMPLATE,
            "Congratulations!<br/> You have successfully signed up. Click this <a href=%s>link</a> to activate your account.");
        properties.put(Configuration.PASSWORD_RESET_EMAIL_SUBJECT, "Password reset request");
        properties.put(Configuration.PASSWORD_RESET_EMAIL_TEMPLATE,
            "We have received a request to reset your password. If you didn't issue it, simply ignore this message. " +
                "If you did, follow this <a href=%s>link</a>.");
    }

    @Test
    public void readsProperConfiguration() {
        Configuration configuration = new Configuration(properties);
        MatcherAssert.assertThat("Does not read proper configuration", Configuration.fromClassPath(),
            new TypeSafeMatcher<Configuration>() {

                @Override
                protected boolean matchesSafely(Configuration item) {
                    return item.port() == configuration.port() &&
                        item.databaseUser().equals(configuration.databaseUser()) &&
                        item.databasePassword().equals(configuration.databasePassword()) &&
                        item.jdbcUrl().equals(configuration.jdbcUrl()) &&
                        item.adminEmail().equals(configuration.adminEmail()) &&
                        item.adminPassword().equals(configuration.adminPassword()) &&
                        item.smtpHost().equals(configuration.smtpHost()) &&
                        item.smtpPort() == configuration.smtpPort() &&
                        item.emailsLinksBase().equals(configuration.emailsLinksBase()) &&
                        item.signUpEmailSubject().equals(configuration.signUpEmailSubject()) &&
                        item.signUpEmailTemplate().equals(configuration.signUpEmailTemplate()) &&
                        item.passwordResetEmailSubject().equals(configuration.passwordResetEmailSubject()) &&
                        item.passwordResetEmailTemplate().equals(configuration.passwordResetEmailTemplate());

                }

                @Override
                public void describeTo(Description description) {
                    description.appendText(configuration.toString());
                }
            });
    }

    @Test
    public void throwsExceptionOnLackingProperty() {
        String key = Configuration.PORT;
        String message = String.format("There is no property associated with %s value", key);
        properties.remove(key);
        MatcherAssert.assertThat("Does not throw exception on lacking property",
            new Configuration(properties)::port, new ThrowsMatcher(message));
    }
}
