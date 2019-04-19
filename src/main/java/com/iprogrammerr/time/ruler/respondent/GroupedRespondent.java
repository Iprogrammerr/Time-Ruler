package com.iprogrammerr.time.ruler.respondent;

import io.javalin.Javalin;

public interface GroupedRespondent {
    void init(String group, Javalin app);
}
