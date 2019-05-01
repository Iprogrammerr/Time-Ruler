package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.model.date.SmartDate;

import java.util.ArrayList;
import java.util.List;

public class DatabaseActivitiesSearch implements ActivitiesSearch {

    private final DatabaseSession session;

    public DatabaseActivitiesSearch(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public List<Activity> ofUserDate(long id, long date) {
        return ofUserDate(id, date, ActivitiesFilter.ALL);
    }

    private List<Activity> ofUserDate(long id, long date, ActivitiesFilter filter) {
        SmartDate smartDate = new SmartDate(date);
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
        }, queryBuilder.toString(), id, smartDate.dayBeginning(), smartDate.dayEnd());
    }

    @Override
    public List<Activity> ofUserDatePlanned(long id, long date) {
        return ofUserDate(id, date, ActivitiesFilter.PLANNED);
    }

    @Override
    public List<Activity> ofUserDateDone(long id, long date) {
        return ofUserDate(id, date, ActivitiesFilter.DONE);
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

    private enum ActivitiesFilter {
        ALL, DONE, PLANNED
    }
}
