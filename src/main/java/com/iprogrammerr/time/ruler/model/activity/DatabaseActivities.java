package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.Record;

import java.sql.ResultSet;

public class DatabaseActivities implements Activities {

    private final DatabaseSession session;

    public DatabaseActivities(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public long create(Activity activity) {
        return session.create(
            new Record(Activity.TABLE)
                .put(Activity.NAME, activity.name)
                .put(Activity.USER_ID, activity.userId)
                .put(Activity.START_DATE, activity.startDate)
                .put(Activity.END_DATE, activity.endDate)
                .put(Activity.DONE, activity.done)
        );
    }

    @Override
    public void update(Activity activity) {
        session.update(
            new Record(Activity.TABLE)
                .put(Activity.NAME, activity.name)
                .put(Activity.USER_ID, activity.userId)
                .put(Activity.START_DATE, activity.startDate)
                .put(Activity.END_DATE, activity.endDate)
                .put(Activity.DONE, activity.done)
            ,
            "id = ?", activity.id
        );
    }

    @Override
    public void delete(long id) {
        session.delete("activity", "id = ?", id);
    }

    @Override
    public Activity activity(long id) {
        return session.select(r -> {
            if (r.next()) {
                return new Activity(r);
            }
            throw new RuntimeException(String.format("There is no activity associated with %d id", id));
        }, "SELECT * from activity WHERE id = ?", id);
    }

    @Override
    public boolean exists(long id) {
        return session.select(ResultSet::next, "SELECT id from activity WHERE id = ?", id);
    }

    @Override
    public boolean belongsToUser(long userId, long activityId) {
        return session.select(ResultSet::next, "SELECT id from activity WHERE user_id = ? AND id = ?",
            userId, activityId);
    }

    @Override
    public void setDone(long id, boolean done) {
        session.update(new Record(Activity.TABLE).put(Activity.DONE, done), "id = ?", id);
    }
}
