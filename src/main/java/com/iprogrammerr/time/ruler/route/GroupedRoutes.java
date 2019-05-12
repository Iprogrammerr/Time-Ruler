package com.iprogrammerr.time.ruler.route;

import io.javalin.Javalin;

public interface GroupedRoutes {
    void init(String group, Javalin app);
}
