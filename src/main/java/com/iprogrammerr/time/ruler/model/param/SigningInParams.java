package com.iprogrammerr.time.ruler.model.param;

import com.iprogrammerr.time.ruler.model.TypedMap;

import java.util.List;
import java.util.Map;

public class SigningInParams {

    public final String emailName;
    public final boolean activation;
    public final boolean farewell;
    public final boolean newPassword;
    public final boolean nonExistentUser;
    public final boolean inactiveAccount;
    public final boolean notUserPassword;
    public final boolean invalidPassword;

    public SigningInParams(String emailName, boolean activation, boolean farewell, boolean newPassword,
        boolean nonExistentUser, boolean inactiveAccount, boolean notUserPassword, boolean invalidPassword) {
        this.emailName = emailName;
        this.activation = activation;
        this.farewell = farewell;
        this.newPassword = newPassword;
        this.nonExistentUser = nonExistentUser;
        this.inactiveAccount = inactiveAccount;
        this.notUserPassword = notUserPassword;
        this.invalidPassword = invalidPassword;
    }

    public static SigningInParams fromQuery(Map<String, List<String>> queryParams) {
        TypedMap params = new TypedMap(queryParams);
        return new SigningInParams(params.stringValue(QueryParams.EMAIL_NAME),
            params.booleanValue(QueryParams.ACTIVATION), params.booleanValue(QueryParams.FAREWELL),
            params.booleanValue(QueryParams.NEW_PASSWORD), params.booleanValue(QueryParams.NON_EXISTENT_USER),
            params.booleanValue(QueryParams.INACTIVE_ACCOUNT), params.booleanValue(QueryParams.NOT_USER_PASSWORD),
            params.booleanValue(QueryParams.INVALID_PASSWORD));
    }

    @Override
    public boolean equals(Object object) {
        boolean equal;
        if (object == this) {
            equal = true;
        } else if (object != null && SigningInParams.class.isAssignableFrom(object.getClass())) {
            SigningInParams other = (SigningInParams) object;
            equal = emailName.equals(other.emailName) && activation == other.activation &&
                farewell == other.farewell && newPassword == other.newPassword && nonExistentUser == other.nonExistentUser &&
                inactiveAccount == other.inactiveAccount && notUserPassword == other.notUserPassword &&
                invalidPassword == other.invalidPassword;
        } else {
            equal = false;
        }
        return equal;
    }
}
