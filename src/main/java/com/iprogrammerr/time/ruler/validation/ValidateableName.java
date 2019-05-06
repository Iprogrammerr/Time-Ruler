package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.model.Initialization;

public class ValidateableName implements Validateable<String> {

    private final String name;
    private final Initialization<Boolean> valid;

    public ValidateableName(String name, int minLength, boolean allowSpaces) {
        this.name = name.trim();
        this.valid = new Initialization<>(() -> {
            boolean valid = name.length() >= minLength;
            if (valid) {
                for (char c : name.toCharArray()) {
                    if (!Character.isLetter(c)) {
                        valid = (allowSpaces && Character.isSpaceChar(c));
                        if (!valid) {
                            break;
                        }
                    }
                }
            }
            return valid;
        });
    }

    public ValidateableName(String name, boolean allowSpaces) {
        this(name, 3, allowSpaces);
    }

    public ValidateableName(String name) {
        this(name, false);
    }

    @Override
    public String value() {
        if (isValid()) {
            return name;
        }
        throw new RuntimeException(String.format("%s is not a validView name", name));
    }

    @Override
    public boolean isValid() {
        return valid.value();
    }
}
