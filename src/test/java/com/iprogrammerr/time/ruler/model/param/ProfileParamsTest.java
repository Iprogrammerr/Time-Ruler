package com.iprogrammerr.time.ruler.model.param;

import com.iprogrammerr.time.ruler.tool.ConvertableMap;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Random;

public class ProfileParamsTest {

    @Test
    public void readsQueryParams() {
        Random random = new Random();
        RandomStrings randomStrings = new RandomStrings(random);
        ProfileParams params = new ProfileParams(randomStrings.email(), randomStrings.name(),
            random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(),
            random.nextBoolean(), random.nextBoolean(), random.nextBoolean());
        ConvertableMap paramsMap = new ConvertableMap()
            .put(QueryParams.NAME, params.name).put(QueryParams.EMAIL, params.email)
            .put(QueryParams.EMAIL_CHANGED, params.emailChanged).put(QueryParams.EMAIL_TAKEN, params.emailTaken)
            .put(QueryParams.NAME_CHANGED, params.nameChanged).put(QueryParams.NAME_TAKEN, params.nameTaken)
            .put(QueryParams.NOT_USER_PASSWORD, params.notUserPassword)
            .put(QueryParams.INVALID_OLD_PASSWORD, params.invalidOldPassword)
            .put(QueryParams.INVALID_NEW_PASSWORD, params.invalidNewPassword);
        MatcherAssert.assertThat("Should be the same", params,
            Matchers.equalTo(ProfileParams.fromQuery(paramsMap.getConverted())));
    }
}
