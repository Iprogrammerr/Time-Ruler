package com.iprogrammerr.time.ruler.model.day;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.Record;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseDays implements Days {

    private final DatabaseSession session;

    public DatabaseDays(DatabaseSession session) {
        this.session = session;
    }

    @Override
    public List<Day> userFrom(long id, long date) {
        return session.select(r -> {
            List<Day> days = new ArrayList<>();
            while (r.next()) {
                days.add(new Day(r));
            }
            return days;
        }, "SELECT * from day where date >= ?", modifiedDate(date, 0, 0, 0));
    }

    private long modifiedDate(long date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTimeInMillis();
    }

    @Override
    public List<Day> userTo(long id, long date) {
        return session.select(r -> {
            List<Day> days = new ArrayList<>();
            while (r.next()) {
                days.add(new Day(r));
            }
            return days;
        }, "SELECT * from day where date <= ? ORDER BY date ASC", modifiedDate(date, 23, 59, 59));
    }

    @Override
    public long createForUser(long id, long date) {
        return session.create(new Record(Day.TABLE).put(Day.USER_ID, id).put(Day.DATE, date));
    }

    @Override
    public boolean ofUserExists(long id, long date) {
        long from = modifiedDate(date, 0, 0, 0);
        long to = modifiedDate(date, 23, 59, 59);
        return session.select(
            ResultSet::next,
            "SELECT id from day WHERE user_id = ? AND date >= ? AND date <= ? ORDER by date ASC",
            id, from, to
        );
    }
}
