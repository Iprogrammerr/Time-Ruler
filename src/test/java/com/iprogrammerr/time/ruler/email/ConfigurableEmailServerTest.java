package com.iprogrammerr.time.ruler.email;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import com.iprogrammerr.time.ruler.Configuration;
import com.iprogrammerr.time.ruler.matcher.MimeMessageMatcher;
import com.iprogrammerr.time.ruler.tool.RandomEmails;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;

import javax.mail.internet.MimeMessage;

//TODO find a way to make smtp tests pass on travis
public class ConfigurableEmailServerTest {

    @Rule
    public final GreenMailRule rule = new GreenMailRule(ServerSetup.SMTP);
    private final Configuration configuration = Configuration.fromClassPath();

    //@Before
    public void setup() {
        rule.setUser(configuration.adminEmail(), configuration.adminPassword());
    }

    //@Test
    public void sendsEmail() {
        ConfigurableEmailServer emailServer = new ConfigurableEmailServer(
            configuration.adminEmail(), configuration.adminPassword(), configuration.smtpHost(),
            configuration.smtpPort()
        );
        Email email = new RandomEmails().email();
        emailServer.send(email);
        MimeMessage received = rule.getReceivedMessages()[0];
        MatcherAssert.assertThat(
            "Should receive sent email", received,
            new MimeMessageMatcher(configuration.adminEmail(), email.recipient, email.subject, email.text)
        );
    }
}
