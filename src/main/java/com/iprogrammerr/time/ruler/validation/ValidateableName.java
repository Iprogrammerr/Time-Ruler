package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.model.Initialization;

public class ValidateableName implements Validateable<String> {

    private final String name;
    private final int minLength;
    private final Initialization<Boolean> valid;

    public ValidateableName(String name, int minLength) {
        this.name = name == null ? "" : name;
        this.minLength = minLength;
        this.valid = new Initialization<>(() -> {
            boolean valid = name.length() >= minLength;
            if (valid) {
                for (char c : name.toCharArray()) {
                    if (!Character.isLetter(c)) {
                        valid = false;
                        break;
                    }
                }
            }
            return valid;
        });
    }

    public ValidateableName(String name) {
        this(name, 3);
    }

    @Override
    public String value() {
        if (isValid()) {
            return name;
        }
        throw new RuntimeException(String.format("%s is not a valid name", name));
    }

    @Override
    public boolean isValid() {
        return valid.value();
    }
}
