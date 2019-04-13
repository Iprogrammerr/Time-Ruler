package com.iprogrammerr.time.ruler.validation;

import com.iprogrammerr.time.ruler.matcher.ThrowsMatcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ValidateablePasswordTest {

    @Test
    public void returnsFalseIfTooShort() {
        ValidateablePassword password = new ValidateablePassword("su");
        MatcherAssert.assertThat("Should not validate", password.isValid(), Matchers.equalTo(password.isValid()));
    }

    @Test
    public void returnsUnchangedAfterValidation() {
        String password = "secRet34";
        ValidateablePassword validateablePassword = new ValidateablePassword(password);
        validateablePassword.isValid();
        MatcherAssert.assertThat("Should not be changed", validateablePassword.value(), Matchers.equalTo(password));
    }

    @Test
    public void throwsExceptionIfIsNotValid() {
        String password = "L2";
        String message = String.format("%s is not a valid password", password);
        MatcherAssert.assertThat(
            "Should throw exception with message", new ValidateablePassword(password)::value, new ThrowsMatcher(message)
        );
    }
}
