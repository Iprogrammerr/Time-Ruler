package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.time.ruler.database.DatabaseSession;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivitiesSearch implements ActivitiesSearch {

    private static final long DAY_SECONDS = 24 * 3600;
    private final DatabaseSession session;

    public DatabaseActivitiesSearch(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public List<Activity> userDayActivities(long id, long dayStart) {
        return userDayActivities(id, dayStart, ActivitiesFilter.ALL);
    }

    private List<Activity> userDayActivities(long id, long dayStart, ActivitiesFilter filter) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM activity WHERE ")
            .append("user_id = ? AND start_date >= ? AND start_date <= ?");
        if (filter != ActivitiesFilter.ALL) {
            queryBuilder.append(" AND done = ").append(filter == ActivitiesFilter.DONE ? "1" : "0");
        }
        queryBuilder.append(" ORDER BY start_date ASC");
        return session.select(r -> {
            List<Activity> activities = new ArrayList<>();
            while (r.next()) {
                activities.add(new Activity(r));
            }
            return activities;
        }, queryBuilder.toString(), id, dayStart, dayStart + DAY_SECONDS);
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
        String query = new StringBuilder("SELECT * FROM activity WHERE user_id = ? ")
            .append("ORDER BY start_date ").append(orderType(ascending))
            .append(" LIMIT ? OFFSET ?")
            .toString();
        return session.select(r -> {
            List<Activity> activities = new ArrayList<>();
            while (r.next()) {
                activities.add(new Activity(r));
            }
            return activities;
        }, query, id, limit, offset);
    }

    private String orderType(boolean ascending) {
        return ascending ? "ASC" : "DESC";
    }

    @Override
    public int matches(long userId) {
        return session.select(r -> {
            r.next();
            return r.getInt(1);
        }, "SELECT COUNT(id) FROM activity WHERE user_id = ?", userId);
    }

    @Override
    public int matches(long userId, String pattern) {
        return session.select(r -> {
                r.next();
                return r.getInt(1);
            }, "SELECT COUNT(id) FROM activity WHERE user_id = ? AND LOWER(name) LIKE LOWER(?)",
            userId, pattern + "%");
    }

    @Override
    public List<Activity> userActivities(long id, String pattern, int offset, int limit, boolean ascending) {
        String query = new StringBuilder("SELECT * FROM activity ")
            .append("WHERE user_id = ? AND LOWER(name) LIKE LOWER(?) ")
            .append("ORDER BY start_date ").append(orderType(ascending))
            .append(" LIMIT ? OFFSET ?")
            .toString();
        return session.select(r -> {
            List<Activity> activities = new ArrayList<>();
            while (r.next()) {
                activities.add(new Activity(r));
            }
            return activities;
        }, query, id, pattern + "%", limit, offset);
    }

    private enum ActivitiesFilter {
        ALL, DONE, PLANNED
    }
}
