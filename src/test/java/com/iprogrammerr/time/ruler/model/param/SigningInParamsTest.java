package com.iprogrammerr.time.ruler.model.param;

import com.iprogrammerr.time.ruler.tool.ConvertableMap;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Random;

public class SigningInParamsTest {

    @Test
    public void readsQueryParams() {
        Random random = new Random();
        RandomStrings randomStrings = new RandomStrings(random);
        SigningInParams params = new SigningInParams(
            random.nextBoolean() ? randomStrings.email() : randomStrings.name(),
            random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
            random.nextBoolean(), random.nextBoolean(), random.nextBoolean());
        ConvertableMap paramsMap = new ConvertableMap()
            .put(QueryParams.EMAIL_NAME, params.emailName).put(QueryParams.ACTIVATION, params.activation)
            .put(QueryParams.FAREWELL, params.farewell).put(QueryParams.NEW_PASSWORD, params.newPassword)
            .put(QueryParams.NON_EXISTENT_USER, params.nonExistentUser)
            .put(QueryParams.INACTIVE_ACCOUNT, params.inactiveAccount)
            .put(QueryParams.NOT_USER_PASSWORD, params.notUserPassword)
            .put(QueryParams.INVALID_PASSWORD, params.invalidPassword);
        MatcherAssert.assertThat("Should be equal", params,
            Matchers.equalTo(SigningInParams.fromQuery(paramsMap.getConverted())));
    }
}
