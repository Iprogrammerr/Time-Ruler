package com.iprogrammerr.time.ruler.validation;

public class ValidateablePassword implements Validateable<String> {

    private final String password;
    private final int minLength;

    public ValidateablePassword(String password, int minLength) {
        this.password = password;
        this.minLength = minLength;
    }

    public ValidateablePassword(String password) {
        this(password, 8);
    }


    @Override
    public String value() {
        return password;
    }

    @Override
    public boolean isValid() {
        return password.length() >= minLength;
    }
}
