package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ValidateableNameTest {

    @Test
    public void returnsFalseIfNameIsTooShort() {
        returnsFalse(new ValidateableName("ab"));
    }

    private void returnsFalse(ValidateableName name) {
        MatcherAssert.assertThat("Name should not be valid", name.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsFalseWithNonLetters() {
        returnsFalse(new ValidateableName("a24"));
    }

    @Test
    public void acceptsNonAscii() {
        ValidateableName name = new ValidateableName("Sźcą");
        MatcherAssert.assertThat("Should accept non ascii letters", name.isValid(), Matchers.equalTo(true));
    }

    @Test
    public void returnsUnchangedAfterValidation() {
        String name = "name";
        ValidateableName validateableName = new ValidateableName(name);
        validateableName.isValid();
        MatcherAssert.assertThat("Should not be changed", validateableName.value(), Matchers.equalTo(name));
    }

    @Test
    public void throwsExceptionIfIsNotValid() {
        String name = "L5_";
        String message = String.format("%s is not a valid name", name);
        MatcherAssert.assertThat(
            "Should throw exception with message", new ValidateableName(name)::value, new ThrowsMatcher(message)
        );
    }
}
