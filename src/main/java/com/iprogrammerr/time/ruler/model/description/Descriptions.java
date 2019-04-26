package com.iprogrammerr.time.ruler.model.description;

import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;

public interface Descriptions {

    long create(Description description);

    DescribedActivity describedActivity(long activityId);
}
