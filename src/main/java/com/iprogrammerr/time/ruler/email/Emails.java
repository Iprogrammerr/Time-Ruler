package com.iprogrammerr.time.ruler.email;

public class Emails implements EmailServer {

    private final EmailServer server;
    private final String activationLinkBase;
    private final String signUpEmailSubject;
    private final String singUpEmailTemplate;

    public Emails(EmailServer server, String activationLinkBase, String signUpEmailSubject, String singUpEmailTemplate) {
        this.server = server;
        this.activationLinkBase = activationLinkBase;
        this.signUpEmailSubject = signUpEmailSubject;
        this.singUpEmailTemplate = singUpEmailTemplate;
    }

    @Override
    public void send(Email email) {
        server.send(email);
    }

    public void sendSignUpEmail(String recipient, String activationLinkSuffix) {
        String body = String.format(singUpEmailTemplate, activationLinkBase + activationLinkSuffix);
        server.send(new Email(recipient, signUpEmailSubject, body));
    }
}
