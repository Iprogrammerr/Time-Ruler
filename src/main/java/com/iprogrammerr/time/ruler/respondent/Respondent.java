package com.iprogrammerr.time.ruler.respondent;

import io.javalin.Javalin;

public interface Respondent {
    void init(Javalin app);
}
