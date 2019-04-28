package com.iprogrammerr.time.ruler.model.activity;

import java.util.List;

public interface Dates {

    long userFirstActivity(long id);

    List<Long> userPlannedDays(long id, long start, long end);
}
