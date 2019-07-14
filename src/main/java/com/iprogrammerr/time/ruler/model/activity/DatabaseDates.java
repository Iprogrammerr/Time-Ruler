package com.iprogrammerr.time.ruler.model.activity;

import com.iprogrammerr.smart.query.QueryFactory;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDates implements Dates {

    private final QueryFactory factory;

    public DatabaseDates(QueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public long userFirstActivity(long id) {
        return factory.newQuery().dsl()
            .select().min(Activity.START_DATE).from(Activity.TABLE)
            .where(Activity.USER_ID).equal().value(id)
            .query()
            .fetch(r -> {
                long min = 0;
                if (r.next()) {
                    min = r.getLong(1);
                }
                return min;
            });
    }

    @Override
    public List<Long> userPlannedDays(long id, long start, long end) {
        return factory.newQuery().dsl()
            .select(Activity.START_DATE).from(Activity.TABLE)
            .where(Activity.START_DATE).greaterEqual().value(start)
            .and(Activity.END_DATE).lessEqual().value(end)
            .and(Activity.USER_ID).equal().value(id)
            .orderBy(Activity.START_DATE).asc()
            .query()
            .fetch(this::daysDates);
    }

    private List<Long> daysDates(ResultSet result) throws Exception {
        List<Long> days = new ArrayList<>();
        int i = 0;
        while (result.next()) {
            long day = result.getLong(1);
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
    }
}
