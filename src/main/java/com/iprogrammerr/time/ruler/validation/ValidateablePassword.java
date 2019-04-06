package com.iprogrammerr.time.ruler.validation;

public class ValidateablePassword implements Validateable<String> {

    private final String password;
    private final int minLength;

    public ValidateablePassword(String password, int minLength) {
        this.password = password == null ? "" : password;
        this.minLength = minLength;
    }

    public ValidateablePassword(String password) {
        this(password, 8);
    }


    @Override
    public String value() {
        if (isValid()) {
            return password;
        }
        throw new RuntimeException(String.format("%s is not a valid password", password));
    }

    @Override
    public boolean isValid() {
        return password.length() >= minLength;
    }
}
