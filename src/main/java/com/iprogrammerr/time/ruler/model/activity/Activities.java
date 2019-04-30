package com.iprogrammerr.time.ruler.model.activity;

import java.util.List;

public interface Activities {

    List<Activity> ofUserDate(long id, long date);

    List<Activity> ofUserDatePlanned(long id, long date);

    List<Activity> ofUserDateDone(long id, long date);

    long create(Activity activity);

    void update(Activity activity);

    void delete(long id);

    Activity activity(long id);

    boolean exists(long id);

    boolean belongsToUser(long userId, long activityId);

    void setDone(long id, boolean done);
}
