package com.iprogrammerr.time.ruler.model.day;

import java.util.List;

public interface Days {

    List<Day> userRange(long id, long from, long to);

    long createForUser(long id, long date);

    boolean ofUserExists(long id, long date);

    long userFirstDate(long id);

    Day ofUser(long id, long date);

    Day ofActivity(long id);
}
