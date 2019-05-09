package com.iprogrammerr.time.ruler.validation;

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
    public void returnsUnchangedValue() {
        String password = "secRet34";
        ValidateablePassword validateablePassword = new ValidateablePassword(password);
        MatcherAssert.assertThat("Should not be changed", validateablePassword.value(), Matchers.equalTo(password));
    }
}
