package com.iprogrammerr.time.ruler.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ThrowsMatcher extends TypeSafeMatcher<ThrowsMatcher.UnreliableRunnable> {

    private final Class<? extends Exception> exception;
    private final String message;

    public ThrowsMatcher(Class<? extends Exception> exception, String message) {
        this.exception = exception;
        this.message = message;
    }

    public ThrowsMatcher(String message) {
        this(Exception.class, message);
    }

    public ThrowsMatcher(Class<? extends Exception> exception) {
        this(exception, "");
    }

    public ThrowsMatcher() {
        this(Exception.class);
    }

    @Override
    protected boolean matchesSafely(UnreliableRunnable item) {
        boolean matched = false;
        try {
            item.run();
        } catch (Exception e) {
            matched = exception.isAssignableFrom(e.getClass());
            if (!message.isEmpty()) {
                matched &= e.getMessage().contains(message);
            }
        }
        return matched;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Matcher that ensures that certain exception is thrown");
    }

    @Override
    protected void describeMismatchSafely(UnreliableRunnable item, Description description) {
        description.appendText(
            String.format("Given function does not throws %s with %s message", exception, message)
        );
    }


    public interface UnreliableRunnable {
        void run() throws Exception;
    }
}
