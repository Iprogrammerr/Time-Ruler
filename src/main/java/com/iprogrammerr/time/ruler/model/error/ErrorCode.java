package com.iprogrammerr.time.ruler.model.error;

public enum ErrorCode {
    UNKNOWN(500);

    public final int httpCode;

    ErrorCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
