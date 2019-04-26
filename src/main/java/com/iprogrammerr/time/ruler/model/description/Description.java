package com.iprogrammerr.time.ruler.model.description;

import java.sql.ResultSet;

public class Description {

    public static final String TABLE = "description";
    public static final String ID = "id";
    public static final String ACTIVITY_ID = "activity_id";
    public static final String CONTENT = "content";

    public final long id;
    public final long activityId;
    public final String content;

    public Description(long id, long activityId, String content) {
        this.id = id;
        this.activityId = activityId;
        this.content = content;
    }

    public Description(long activityId, String content) {
        this(0, activityId, content);
    }

    public Description(ResultSet result) throws Exception {
        this(result.getLong(ID), result.getLong(ACTIVITY_ID), result.getString(CONTENT));
    }
}
