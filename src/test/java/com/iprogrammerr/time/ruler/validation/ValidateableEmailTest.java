package com.iprogrammerr.time.ruler.validation;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ValidateableEmailTest {

    @Test
    public void returnsFalseWithoutAt() {
        returnsFalse(new ValidateableEmail("igor_gmail.com"));
    }

    private void returnsFalse(ValidateableEmail email) {
        MatcherAssert.assertThat("Should return false", email.isValid(), Matchers.equalTo(false));
    }

    @Test
    public void returnsFalseWithoutDomain() {
        returnsFalse(new ValidateableEmail("olek@gmail"));
    }

    @Test
    public void returnsFalseWithEmptyDomain() {
        returnsFalse(new ValidateableEmail("dr@gmail."));
    }

    @Test
    public void returnsFalseWithoutName() {
        returnsFalse(new ValidateableEmail("@mailtrap.com"));
    }

    @Test
    public void returnsFalseWithAtAndDomainOnly() {
        returnsFalse(new ValidateableEmail("@.com"));
    }

    @Test
    public void returnsTrueWithProperEmail() {
        ValidateableEmail email = new ValidateableEmail("ceigor94@gmail.com");
        MatcherAssert.assertThat("Email should be valid", email.isValid(), Matchers.equalTo(true));
    }

    @Test
    public void returnsTrueWithNonAsciiEmail() {
        ValidateableEmail email = new ValidateableEmail("cęśćü@mock.com");
        MatcherAssert.assertThat(
            "Email with non ascii chars should be valid", email.isValid(), Matchers.equalTo(true)
        );
    }

    @Test
    public void returnsUnchangedValue() {
        String email = "email@example.com";
        ValidateableEmail validateableEmail = new ValidateableEmail(email);
        MatcherAssert.assertThat("Should not be changed", validateableEmail.value(), Matchers.equalTo(email));
    }
}
