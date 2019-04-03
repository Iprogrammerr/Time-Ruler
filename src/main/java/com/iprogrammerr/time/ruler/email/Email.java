package com.iprogrammerr.time.ruler.email;

public class Email {

    public final String sender;
    public final String recipent;
    public final String subject;
    public final String text;

    public Email(String sender, String recipent, String subject, String text) {
        this.sender = sender;
        this.recipent = recipent;
        this.subject = subject;
        this.text = text;
    }
}
