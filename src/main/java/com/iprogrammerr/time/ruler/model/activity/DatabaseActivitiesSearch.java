package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.time.ruler.database.DatabaseSession;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivitiesSearch implements ActivitiesSearch {

    private final DatabaseSession session;

    public DatabaseActivitiesSearch(DatabaseSession session) {
        this.session = session;
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
    public int matches(long userId, String pattern) {
        return session.select(r -> {
                r.next();
                return r.getInt(1);
            }, "SELECT COUNT(id) FROM activity WHERE LOWER(name) LIKE LOWER(?)",
            pattern + "%");
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
}
