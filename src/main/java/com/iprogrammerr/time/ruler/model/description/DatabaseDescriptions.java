package com.iprogrammerr.time.ruler.model.description;

import com.iprogrammerr.smart.query.QueryDsl;
import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;

import java.sql.ResultSet;

public class DatabaseDescriptions implements Descriptions {

    private final QueryFactory factory;

    public DatabaseDescriptions(QueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public long create(Description description) {
        return factory.newQuery().dsl()
            .insertInto(Description.TABLE)
            .columns(Description.ACTIVITY_ID, Description.CONTENT)
            .values(description.activityId, description.content)
            .query()
            .executeReturningId();
    }

    @Override
    public DescribedActivity describedActivity(long activityId) {
        return factory.newQuery().dsl()
            .select("a.*, d.content").from(Activity.TABLE).as("a")
            .leftJoin(Description.TABLE).as("d").on("d.activity_id", "a.id")
            .where("a.id").equal().value(activityId)
            .query()
            .fetch(r -> {
                if (r.next()) {
                    String description = r.getString(Description.CONTENT);
                    if (description == null) {
                        description = "";
                    }
                    return new DescribedActivity(new Activity(r), description);
                }
                throw new RuntimeException(String.format("There is no activity associated with %d id",
                    activityId));
            });
    }

    @Override
    public void updateOrCreate(Description description) {
        QueryDsl dsl = factory.newQuery().dsl();
        if (descriptionExists(description.activityId)) {
            dsl.update(Description.TABLE)
                .set(Description.ACTIVITY_ID, description.activityId)
                .set(Description.CONTENT, description.content)
                .where(Description.ACTIVITY_ID).equal().value(description.activityId);
        } else {
            dsl.insertInto(Description.TABLE)
                .columns(Description.ACTIVITY_ID, Description.CONTENT)
                .values(description.activityId, description.content);
        }
        dsl.query().execute();
    }

    @Override
    public void delete(long activityId) {
        factory.newQuery().dsl()
            .delete(Description.TABLE)
            .where(Description.ACTIVITY_ID).equal().value(activityId)
            .query()
            .execute();
    }

    private boolean descriptionExists(long activityId) {
        return factory.newQuery().dsl()
            .select(Description.ID).from(Description.TABLE)
            .where(Description.ACTIVITY_ID).equal().value(activityId)
            .query()
            .fetch(ResultSet::next);
    }
}
