package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.time.ruler.database.DatabaseSession;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDates implements Dates {

    private final DatabaseSession session;

    public DatabaseDates(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public long userFirstActivity(long id) {
        return session.select(r -> {
            long min = 0;
            if (r.next()) {
                min = r.getLong(1);
            }
            return min;
        }, "SELECT MIN(start_date) from activity WHERE user_id = ?", id);
    }

    @Override
    public List<Long> userPlannedDays(long id, long start, long end) {
        String query = new StringBuilder("SELECT start_date from activity ")
            .append("WHERE start_date >= ? AND start_date <= ? ")
            .append("ORDER by start_date ASC")
            .toString();
        return session.select(r -> {
            List<Long> days = new ArrayList<>();
            int i = 0;
            while (r.next()) {
                long day = r.getLong(1);
                if (i == 0) {
                    days.add(day);
                    i++;
                } else {
                    ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(day), ZoneOffset.UTC);
                    ZonedDateTime previousDate = ZonedDateTime.ofInstant(Instant.ofEpochSecond(days.get(i - 1)),
                        ZoneOffset.UTC);
                    if (date.getDayOfMonth() != previousDate.getDayOfMonth()) {
                        days.add(day);
                        i++;
                    }
                }
            }
            return days;
        }, query, start, end);
    }
}
