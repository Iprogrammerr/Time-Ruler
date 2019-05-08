package com.iprogrammerr.time.ruler.model.activity;

import java.util.Optional;

public interface Activities {

    long create(Activity activity);

    void update(Activity activity);

    void delete(long id);

    Optional<Activity> activity(long id);

    boolean belongsToUser(long userId, long activityId);

    void setDone(long id, boolean done);
}
