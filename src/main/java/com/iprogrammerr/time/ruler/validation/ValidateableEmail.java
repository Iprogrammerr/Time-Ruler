package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.model.Initialization;

public class ValidateableEmail implements Validateable<String> {

    private static final int MIN_LENGTH = 8;
    private final String email;
    private final Initialization<Boolean> valid;

    public ValidateableEmail(String email) {
        this.email = email;
        this.valid = new Initialization<>(() -> {
            int atIndex = email.indexOf('@');
            int dotIndex = email.indexOf('.');
            return email.length() >= MIN_LENGTH && atIndex > 2
                && (dotIndex - atIndex) > 1 && (email.length() - dotIndex) > 1;
        });
    }

    @Override
    public String value() {
        if (!isValid()) {
            throw new RuntimeException(String.format("%s is not a valid email", email));
        }
        return email;
    }

    @Override
    public boolean isValid() {
        return valid.value();
    }
}
