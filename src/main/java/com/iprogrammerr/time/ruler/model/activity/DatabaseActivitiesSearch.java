package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.smart.query.QueryDsl;
import com.iprogrammerr.smart.query.QueryFactory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivitiesSearch implements ActivitiesSearch {

    private static final long DAY_SECONDS = 24 * 3600;
    private final QueryFactory factory;

    public DatabaseActivitiesSearch(QueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public List<Activity> userDayActivities(long id, long dayStart) {
        return userDayActivities(id, dayStart, ActivitiesFilter.ALL);
    }

    private List<Activity> userDayActivities(long id, long dayStart, ActivitiesFilter filter) {
        QueryDsl dsl = factory.newQuery().dsl()
            .selectAll().from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(id)
            .and(Activity.START_DATE).greaterEqual().value(dayStart)
            .and(Activity.END_DATE).lessEqual().value(dayStart + DAY_SECONDS);
        if (filter != ActivitiesFilter.ALL) {
            dsl.and(Activity.DONE).equal().value(filter == ActivitiesFilter.DONE ? 1 : 0);
        }
        dsl.orderBy(Activity.START_DATE).asc();
        return dsl.query().fetch(r -> {
            List<Activity> activities = new ArrayList<>();
            while (r.next()) {
                activities.add(new Activity(r));
            }
            return activities;
        });
    }

    @Override
    public List<Activity> userDayPlannedActivities(long id, long dayStart) {
        return userDayActivities(id, dayStart, ActivitiesFilter.PLANNED);
    }

    @Override
    public List<Activity> userDayDoneActivities(long id, long dayStart) {
        return userDayActivities(id, dayStart, ActivitiesFilter.DONE);
    }

    @Override
    public List<Activity> userActivities(long id, int offset, int limit, boolean ascending) {
        QueryDsl dsl = factory.newQuery().dsl()
            .selectAll().from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(id)
            .orderBy(Activity.START_DATE);
        if (ascending) {
            dsl.asc();
        } else {
            dsl.desc();
        }
        return dsl.limit(offset, limit).query().fetch(r -> {
            List<Activity> activities = new ArrayList<>();
            while (r.next()) {
                activities.add(new Activity(r));
            }
            return activities;
        });
    }


    @Override
    public int matching(long userId) {
        return factory.newQuery().dsl()
            .select().count(Activity.ID).from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(userId)
            .query()
            .fetch(r -> {
                r.next();
                return r.getInt(1);
            });
    }

    @Override
    public int matching(long userId, String pattern) {
        return factory.newQuery().dsl()
            .select().count(Activity.ID).from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(userId)
            .and("LOWER(name)").like(loweredPattern(pattern))
            .query()
            .fetch(r -> {
                r.next();
                return r.getInt(1);
            });
    }

    private String loweredPattern(String pattern) {
        return pattern.toLowerCase() + "%";
    }

    @Override
    public List<Activity> userActivities(long id, String pattern, int offset, int limit, boolean ascending) {
        QueryDsl dsl = factory.newQuery().dsl()
            .selectAll().from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(id)
            .and("LOWER(name)").like(loweredPattern(pattern))
            .orderBy(Activity.START_DATE);
        if (ascending) {
            dsl.asc();
        } else {
            dsl.desc();
        }
        return dsl.limit(offset, limit).query().fetch(r -> {
            List<Activity> activities = new ArrayList<>();
            while (r.next()) {
                activities.add(new Activity(r));
            }
            return activities;
        });
    }

    private enum ActivitiesFilter {
        ALL, DONE, PLANNED
    }
}
