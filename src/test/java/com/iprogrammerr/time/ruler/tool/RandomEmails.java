package com.iprogrammerr.time.ruler.tool;

import com.iprogrammerr.time.ruler.email.Email;

import java.util.Random;

public class RandomEmails {

    private static final int MAX_SUBJECT_LENGTH = 50;
    private static final int MAX_TEXT_LENGTH = 500;
    private final RandomStrings strings;
    private final Random random;

    public RandomEmails(RandomStrings strings, Random random) {
        this.strings = strings;
        this.random = random;
    }

    public RandomEmails(Random random) {
        this(new RandomStrings(random), random);
    }

    public RandomEmails() {
        this(new Random());
    }

    public Email email() {
        String to = strings.email();
        String subject = strings.alphanumeric(1 + random.nextInt(MAX_SUBJECT_LENGTH));
        String text = strings.alphanumeric(1 + random.nextInt(MAX_TEXT_LENGTH));
        return new Email(to, subject, text);
    }
}
