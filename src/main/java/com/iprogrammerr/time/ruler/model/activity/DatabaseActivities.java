package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.smart.query.QueryFactory;

import java.sql.ResultSet;
import java.util.Optional;

public class DatabaseActivities implements Activities {

    private final QueryFactory factory;

    public DatabaseActivities(QueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public long create(Activity activity) {
        return factory.newQuery().dsl()
            .insertInto(Activity.TABLE)
            .columns(Activity.NAME, Activity.USER_ID, Activity.START_DATE, Activity.END_DATE, Activity.DONE)
            .values(activity.name, activity.userId, activity.startDate, activity.endDate, activity.done)
            .query()
            .executeReturningId();
    }

    @Override
    public void update(Activity activity) {
        factory.newQuery().dsl()
            .update(Activity.TABLE)
            .set(Activity.NAME, activity.name)
            .set(Activity.USER_ID, activity.userId)
            .set(Activity.START_DATE, activity.startDate)
            .set(Activity.END_DATE, activity.endDate)
            .set(Activity.DONE, activity.done)
            .where(Activity.ID).equal().value(activity.id)
            .query()
            .execute();
    }

    @Override
    public void delete(long id) {
        factory.newQuery().dsl()
            .delete(Activity.TABLE).where(Activity.ID).equal().value(id)
            .query()
            .execute();
    }

    @Override
    public Optional<Activity> activity(long id) {
        return factory.newQuery().dsl()
            .selectAll().from(Activity.TABLE).where(Activity.ID).equal().value(id)
            .query()
            .fetch(r -> {
                Optional<Activity> activity;
                if (r.next()) {
                    activity = Optional.of(new Activity(r));
                } else {
                    activity = Optional.empty();
                }
                return activity;
            });
    }

    @Override
    public boolean belongsToUser(long userId, long activityId) {
        return factory.newQuery().dsl()
            .select(Activity.ID).from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(userId).and(Activity.ID).equal().value(activityId)
            .query()
            .fetch(ResultSet::next);
    }

    @Override
    public void setDone(long id, boolean done) {
        factory.newQuery().dsl()
            .update(Activity.TABLE)
            .set(Activity.DONE, done)
            .where(Activity.ID).equal().value(id)
            .query()
            .execute();
    }
}
