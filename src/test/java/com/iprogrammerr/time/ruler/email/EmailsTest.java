package com.iprogrammerr.time.ruler.email;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import com.iprogrammerr.time.ruler.Configuration;
import com.iprogrammerr.time.ruler.matcher.MimeMessageMatcher;
import com.iprogrammerr.time.ruler.mock.RandomEmails;
import com.iprogrammerr.time.ruler.mock.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.mail.internet.MimeMessage;
import java.util.Random;

public class EmailsTest {

    private static final int MAX_ACTIVATION_LINK_SUFFIX_LENGTH = 50;
    @Rule
    public final GreenMailRule rule = new GreenMailRule(ServerSetup.ALL);
    private Configuration configuration;
    private Emails emails;

    @Before
    public void setup() {
        configuration = Configuration.fromClassPath();
        rule.setUser(configuration.adminEmail(), configuration.adminPassword());
        ConfigurableEmailServer emailServer = new ConfigurableEmailServer(
            configuration.adminEmail(), configuration.adminPassword(), configuration.smtpHost(),
            configuration.smtpPort()
        );
        emails = new Emails(
            emailServer, configuration.activationLinkBase(), configuration.signUpEmailSubject(),
            configuration.signUpEmailTemplate()
        );
    }

    @Test
    public void sendsEmail() {
        Email email = new RandomEmails().email();
        emails.send(email);
        MimeMessage received = rule.getReceivedMessages()[0];
        MatcherAssert.assertThat(
            "Should receive sent email", received,
            new MimeMessageMatcher(configuration.adminEmail(), email.recipient, email.subject, email.text)
        );
    }

    @Test
    public void sendsSignUpEmail() {
        RandomStrings strings = new RandomStrings();
        String recipient = strings.email();
        String linkSuffix = strings.alphanumeric(1 + new Random().nextInt(MAX_ACTIVATION_LINK_SUFFIX_LENGTH));
        String text = String.format(
            configuration.signUpEmailTemplate(), configuration.activationLinkBase() + linkSuffix
        );
        emails.sendSignUpEmail(recipient, linkSuffix);
        MimeMessage received = rule.getReceivedMessages()[0];
        MatcherAssert.assertThat(
            "Should receive proper sign up email", received,
            new MimeMessageMatcher(configuration.adminEmail(), recipient, configuration.signUpEmailSubject(), text)
        );
    }
}
