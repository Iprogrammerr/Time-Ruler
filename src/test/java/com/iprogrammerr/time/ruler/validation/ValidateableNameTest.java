package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.tool.RandomStrings;
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
    public void acceptsSpaces() {
        RandomStrings strings = new RandomStrings();
        ValidateableName name = new ValidateableName(strings.alphabetic(3) + " " + strings.alphabetic(5),
            true);
        MatcherAssert.assertThat("Should accept spaces", name.isValid(), Matchers.equalTo(true));
    }

    @Test
    public void returnsUnchangedValue() {
        String name = "name";
        ValidateableName validateableName = new ValidateableName(name);
        MatcherAssert.assertThat("Should not be changed", validateableName.value(), Matchers.equalTo(name));
    }
}
