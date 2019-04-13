package com.iprogrammerr.time.ruler.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Collections;
import java.util.List;

public class MimeMessageMatcher extends TypeSafeMatcher<MimeMessage> {

    private final String sender;
    private final List<String> recipients;
    private final String subject;
    private final String contentType;
    private final String text;

    public MimeMessageMatcher(String sender, List<String> recipients, String subject, String contentType, String text) {
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.contentType = contentType;
        this.text = text;
    }

    public MimeMessageMatcher(String sender, String recipient, String subject, String contentType, String text) {
        this(sender, Collections.singletonList(recipient), subject, contentType, text);
    }

    public MimeMessageMatcher(String sender, String recipient, String subject, String text) {
        this(sender, recipient, subject, "text/html", text);
    }

    @Override
    protected boolean matchesSafely(MimeMessage item) {
        boolean match;
        try {
            match = item.getFrom()[0].toString().equals(sender) && item.getSubject().equals(subject);
            if (match) {
                BodyPart part = ((MimeMultipart) item.getContent()).getBodyPart(0);
                match = part.getContent().equals(text)
                    && part.getContentType().contains(contentType);
            }
            if (match) {
                for (Address r : item.getAllRecipients()) {
                    match = recipients.contains(r.toString());
                    if (!match) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            match = false;
        }
        return match;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getClass().getName());
    }

    @Override
    protected void describeMismatchSafely(MimeMessage item, Description mismatchDescription) {
        mismatchDescription.appendValue(item).appendText("Does not has required attributes");
    }
}
