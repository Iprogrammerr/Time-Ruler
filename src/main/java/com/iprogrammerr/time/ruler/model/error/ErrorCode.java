package com.iprogrammerr.time.ruler.model.error;

import java.net.HttpURLConnection;

public enum ErrorCode {
    UNKNOWN(HttpURLConnection.HTTP_INTERNAL_ERROR), GREATER_START_TIME, ACTIVITIES_INTERSECTS, ACTIVITY_NON_EXISTENT_ID,
    ACTIVITY_NOT_OWNED, INVALID_ACTIVATION_LINK;

    public final int httpCode;

    ErrorCode(int httpCode) {
        this.httpCode = httpCode;
    }

    ErrorCode() {
        this(HttpURLConnection.HTTP_BAD_REQUEST);
    }
}
