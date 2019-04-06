package com.iprogrammerr.time.ruler.email;

public class Email {

    public final String recipient;
    public final String subject;
    public final String text;

    public Email(String recipient, String subject, String text) {
        this.recipient = recipient;
        this.subject = subject;
        this.text = text;
    }
}
