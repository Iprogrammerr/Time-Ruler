package com.iprogrammerr.time.ruler.model.param;

import com.iprogrammerr.time.ruler.model.TypedMap;

import java.util.List;
import java.util.Map;

public class ProfileParams {

    public final String email;
    public final String name;
    public final boolean emailChanged;
    public final boolean emailTaken;
    public final boolean nameChanged;
    public final boolean nameTaken;
    public final boolean notUserPassword;
    public final boolean invalidOldPassword;
    public final boolean invalidNewPassword;

    public ProfileParams(String email, String name, boolean emailChanged, boolean emailTaken, boolean nameChanged,
        boolean nameTaken, boolean notUserPassword, boolean invalidOldPassword, boolean invalidNewPassword) {
        this.email = email;
        this.name = name;
        this.emailChanged = emailChanged;
        this.emailTaken = emailTaken;
        this.nameChanged = nameChanged;
        this.nameTaken = nameTaken;
        this.notUserPassword = notUserPassword;
        this.invalidOldPassword = invalidOldPassword;
        this.invalidNewPassword = invalidNewPassword;
    }

    public static ProfileParams fromQuery(Map<String, List<String>> queryParams) {
        TypedMap params = new TypedMap(queryParams);
        return new ProfileParams(params.stringValue(QueryParams.EMAIL), params.stringValue(QueryParams.NAME),
            params.booleanValue(QueryParams.EMAIL_CHANGED), params.booleanValue(QueryParams.EMAIL_TAKEN),
            params.booleanValue(QueryParams.NAME_CHANGED), params.booleanValue(QueryParams.NAME_TAKEN),
            params.booleanValue(QueryParams.NOT_USER_PASSWORD), params.booleanValue(QueryParams.INVALID_OLD_PASSWORD),
            params.booleanValue(QueryParams.INVALID_NEW_PASSWORD));
    }

    @Override
    public boolean equals(Object object) {
        boolean equal;
        if (object == this) {
            equal = true;
        } else if (object != null && ProfileParams.class.isAssignableFrom(object.getClass())) {
            ProfileParams other = (ProfileParams) object;
            equal = name.equals(other.name) && email.equals(other.email) && emailChanged == other.emailChanged &&
                emailTaken == other.emailTaken && nameChanged == other.nameChanged && nameTaken == other.nameTaken &&
                notUserPassword == other.notUserPassword && invalidOldPassword == other.invalidOldPassword &&
                invalidNewPassword == other.invalidNewPassword;
        } else {
            equal = false;
        }
        return equal;
    }
}