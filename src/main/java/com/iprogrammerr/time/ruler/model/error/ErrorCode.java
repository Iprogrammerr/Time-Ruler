package com.iprogrammerr.time.ruler.model.error;

import com.iprogrammerr.time.ruler.validation.ValidateableEmail;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateablePassword;

import java.net.HttpURLConnection;

public enum ErrorCode {
    UNKNOWN(HttpURLConnection.HTTP_INTERNAL_ERROR), GREATER_START_TIME, ACTIVITIES_INTERSECTS, ACTIVITY_NON_EXISTENT_ID,
    ACTIVITY_NOT_OWNED, INVALID_ACTIVATION_LINK, INVALID_EMAIL, INVALID_NAME, INVALID_PASSWORD;

    public final int httpCode;

    ErrorCode(int httpCode) {
        this.httpCode = httpCode;
    }

    ErrorCode() {
        this(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static ErrorCode invalid(ValidateableEmail email, ValidateableName name, ValidateablePassword password) {
        ErrorCode code;
        if (!email.isValid()) {
            code = INVALID_EMAIL;
        } else if (!name.isValid()) {
            code = INVALID_NAME;
        } else if (!password.isValid()) {
            code = INVALID_PASSWORD;
        } else {
            code = UNKNOWN;
        }
        return code;
    }
}
