package com.iprogrammerr.time.ruler.model.day;

import java.util.List;

public interface Days {

    List<Day> userFrom(long id, long date);

    List<Day> userTo(long id, long date);

    long createForUser(long id, long date);

    boolean ofUserExists(long id, long date);
}
