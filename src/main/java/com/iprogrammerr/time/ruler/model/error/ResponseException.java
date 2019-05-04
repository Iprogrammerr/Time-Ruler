package com.iprogrammerr.time.ruler.model.error;

public class ResponseException extends RuntimeException {

    public final ErrorCode error;

    public ResponseException(ErrorCode error) {
        this.error = error;
    }
}
