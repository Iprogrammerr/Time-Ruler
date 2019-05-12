package com.iprogrammerr.time.ruler.respondent;

import java.net.HttpURLConnection;

public class HtmlResponse {

    public final int code;
    public final String body;

    public HtmlResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public HtmlResponse(String body) {
        this(HttpURLConnection.HTTP_OK, body);
    }
}
