package com.iprogrammerr.time.ruler.model;

public enum QueryParamKey {
    FAREWELL("farewell"), NEW_PASSWORD("newPassword"), EMAIL_NAME("emailName"),
    EMAIL("email"), NAME("name"), NON_EXISTENT_USER("nonExistentUser"),
    INACTIVE_ACCOUNT("inactiveAccount"), INVALID_EMAIL("invalidEmail"),
    INVALID_NAME("invalidName"), INVALID_PASSWORD("invalidPassword"),
    NOT_USER_PASSWORD("notUserPassword"), ACTIVATION("activation"), HASH("hash"),
    EMAIL_SENT("emailSent"), EMAIL_TAKEN("emailTaken"), NAME_TAKEN("nameTaken");

    public final String value;

    QueryParamKey(String value) {
        this.value = value;
    }
}
