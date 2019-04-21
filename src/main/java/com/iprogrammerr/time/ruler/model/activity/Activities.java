package com.iprogrammerr.time.ruler.model.activity;

import java.util.List;

public interface Activities {

    List<Activity> ofUserDate(long id, long date);

    List<Activity> ofUserDatePlanned(long id, long date);

    List<Activity> ofUserDateDone(long id, long date);

    long create(Activity activity);

    Activity activity(long id);
}
