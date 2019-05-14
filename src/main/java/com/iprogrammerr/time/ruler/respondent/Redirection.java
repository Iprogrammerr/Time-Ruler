package com.iprogrammerr.time.ruler.respondent;

public class Redirection {

    public final String location;

    public Redirection(String location) {
        this.location = location;
    }

    public Redirection(String prefix, String location) {
        this("/" + prefix + location);
    }

    public String prefixed(String prefix) {
        return "/" + prefix + location;
    }
}
