package com.iprogrammerr.time.ruler.model.description;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.Record;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;

public class DatabaseDescriptions implements Descriptions {

    private final DatabaseSession session;

    public DatabaseDescriptions(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public long create(Description description) {
        return session.create(new Record(Description.TABLE).put(Description.ACTIVITY_ID, description.activityId)
            .put(Description.CONTENT, description.content));
    }

    @Override
    public DescribedActivity describedActivity(long activityId) {
        String query = new StringBuilder("SELECT a.*, d.content from activity a ")
            .append("LEFT JOIN description d ON d.activity_id = a.id ")
            .append("WHERE a.id = ?")
            .toString();
        return session.select(r -> {
            if (r.next()) {
                String description = r.getString(Description.CONTENT);
                if (description == null) {
                    description = "";
                }
                return new DescribedActivity(new Activity(r), description);
            }
            throw new RuntimeException(String.format("There is no activity associated with %d id", activityId));
        }, query, activityId);
    }
}
