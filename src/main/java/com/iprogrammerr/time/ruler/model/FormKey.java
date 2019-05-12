package com.iprogrammerr.time.ruler.model;

public enum FormKey {
    EMAIL("email"), EMAIL_NAME("emailName"), NAME("name"), PASSWORD("password"),
    OLD_PASSWORD("oldPassword"), NEW_PASSWORD("newPassword"), START_TIME("startTime"),
    END_TIME("endTime"), DESCRIPTION("description");

    public final String value;

    FormKey(String value) {
        this.value = value;
    }
}
