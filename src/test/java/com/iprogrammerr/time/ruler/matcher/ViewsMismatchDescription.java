package com.iprogrammerr.time.ruler.matcher;

import org.hamcrest.Description;

public class ViewsMismatchDescription {

    private final String actualView;
    private final String expectedView;

    public ViewsMismatchDescription(String actualView, String expectedView) {
        this.actualView = actualView;
        this.expectedView = expectedView;
    }

    public void append(Description description) {
        description.appendText(actualView).appendText(System.lineSeparator())
            .appendText(" is not ").appendText(System.lineSeparator()).appendText(expectedView);
    }
}
