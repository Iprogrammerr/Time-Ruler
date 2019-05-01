package com.iprogrammerr.time.ruler.model.activity;

public interface Activities {

    long create(Activity activity);

    void update(Activity activity);

    void delete(long id);

    Activity activity(long id);

    boolean exists(long id);

    boolean belongsToUser(long userId, long activityId);

    void setDone(long id, boolean done);
}
