package com.iprogrammerr.time.ruler.model.day;

import com.iprogrammerr.time.ruler.database.DatabaseSession;
import com.iprogrammerr.time.ruler.database.Record;
import com.iprogrammerr.time.ruler.model.date.SmartDate;

import java.sql.ResultSet;
import java.util.ArrayList;
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
        }, "SELECT * from day where date >= ?", new SmartDate(date).dayBeginning());
    }

    @Override
    public List<Day> userTo(long id, long date) {
        return session.select(r -> {
            List<Day> days = new ArrayList<>();
            while (r.next()) {
                days.add(new Day(r));
            }
            return days;
        }, "SELECT * from day where date <= ? ORDER BY date ASC", new SmartDate(date).dayEnd());
    }

    @Override
    public long createForUser(long id, long date) {
        return session.create(new Record(Day.TABLE).put(Day.USER_ID, id).put(Day.DATE, date));
    }

    @Override
    public boolean ofUserExists(long id, long date) {
        SmartDate smartDate = new SmartDate(date);
        return session.select(
            ResultSet::next,
            "SELECT id from day WHERE user_id = ? AND date >= ? AND date <= ? ORDER by date ASC",
            id, smartDate.dayBeginning(), smartDate.dayEnd()
        );
    }

    @Override
    public long userFirstDate(long id) {
        return session.select(r -> {
            long min = 0;
            if (r.next()) {
                min = r.getLong(1);
            }
            return min;
        }, "SELECT MIN(date) from day WHERE user_id = ?", id);
    }

    @Override
    public Day ofUser(long id, long date) {
        SmartDate smartDate = new SmartDate(date);
        return session.select(r -> {
                if (r.next()) {
                    return new Day(r);
                }
                throw new RuntimeException(String.format("There is no day associated user %d and date %d", id, date));
            }, "SELECT * from day WHERE user_id = ? AND date >= ? AND date <= ?",
            id, smartDate.dayBeginning(), smartDate.dayEnd());
    }
}
