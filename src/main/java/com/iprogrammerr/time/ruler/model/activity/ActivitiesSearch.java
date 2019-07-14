package com.iprogrammerr.time.ruler.model.activity;

import java.util.List;

public interface ActivitiesSearch {

    List<Activity> userDayActivities(long id, long dayStart);

    List<Activity> userDayPlannedActivities(long id, long dayStart);

    List<Activity> userDayDoneActivities(long id, long dayStart);

    List<Activity> userActivities(long id, int offset, int limit, boolean ascending);

    int matching(long userId);

    int matching(long userId, String pattern);

    List<Activity> userActivities(long id, String pattern, int offset, int limit, boolean ascending);
}
