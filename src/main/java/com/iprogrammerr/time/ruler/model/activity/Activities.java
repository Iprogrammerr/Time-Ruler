package com.iprogrammerr.time.ruler.model.activity;

import java.util.List;

public interface Activities {

    List<Activity> ofUserDate(long id, long date);

    long create(Activity activity);
}
