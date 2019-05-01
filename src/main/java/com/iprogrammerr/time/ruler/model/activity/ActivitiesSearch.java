package com.iprogrammerr.time.ruler.model.activity;

import java.util.List;

public interface ActivitiesSearch {

    List<Activity> userActivities(long id, int offset, int limit, boolean ascending);

    int matches(long userId, String pattern);

    List<Activity> userActivities(long id, String pattern, int offset, int limit, boolean ascending);
}
