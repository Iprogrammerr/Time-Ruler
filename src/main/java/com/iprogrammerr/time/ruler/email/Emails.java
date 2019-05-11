package com.iprogrammerr.time.ruler.email;

import com.iprogrammerr.time.ruler.Configuration;

public class Emails implements EmailServer {

    private final EmailServer server;
    private final String emailsLinksBase;
    private final String signUpEmailSubject;
    private final String singUpEmailTemplate;
    private final String passwordResetSubject;
    private final String passwordResetTemplate;

    public Emails(EmailServer server, String emailsLinksBase, String signUpEmailSubject, String singUpEmailTemplate,
        String passwordResetSubject, String passwordResetTemplate) {
        this.server = server;
        this.emailsLinksBase = emailsLinksBase;
        this.signUpEmailSubject = signUpEmailSubject;
        this.singUpEmailTemplate = singUpEmailTemplate;
        this.passwordResetSubject = passwordResetSubject;
        this.passwordResetTemplate = passwordResetTemplate;
    }

    public Emails(EmailServer server, Configuration configuration) {
        this(server, configuration.emailsLinksBase(), configuration.signUpEmailSubject(),
            configuration.signUpEmailTemplate(), configuration.passwordResetEmailSubject(),
            configuration.passwordResetEmailTemplate());
    }

    @Override
    public void send(Email email) {
        server.send(email);
    }

    public void sendSignUpEmail(String recipient, String activationLinkSuffix) {
        String body = String.format(singUpEmailTemplate, emailsLinksBase + activationLinkSuffix);
        server.send(new Email(recipient, signUpEmailSubject, body));
    }

    public void sendPasswordResetEmail(String recipient, String passwordResetLinkSuffix) {
        String body = String.format(passwordResetTemplate, emailsLinksBase + passwordResetLinkSuffix);
        server.send(new Email(recipient, passwordResetSubject, body));
    }
}
