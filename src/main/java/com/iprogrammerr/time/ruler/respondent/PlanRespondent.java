package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlanRespondent implements Respondent {

    private static final long DAY_SECONDS = TimeUnit.DAYS.toSeconds(1);
    private static final String PLAN = "plan";
    private static final String MONTH_TEMPLATE = "month";
    private static final String YEAR_TEMPLATE = "year";
    private static final String DAYS_TEMPLATE = "days";
    private final Identity<Long> identity;
    private final ViewsTemplates viewsTemplates;
    private final Days days;

    public PlanRespondent(Identity<Long> identity, ViewsTemplates viewsTemplates, Days days) {
        this.identity = identity;
        this.viewsTemplates = viewsTemplates;
        this.days = days;
    }

    @Override
    public void init(Javalin app) {
        app.get(PLAN, this::showMainPage);
    }

    private void showMainPage(Context context) {
        if (identity.isValid(context.req)) {
            renderCalendar(context);
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    //TODO consider client timezone!
    private void renderCalendar(Context context) {
        ZonedDateTime currentDate = ZonedDateTime.now();
        Map<String, Object> params = new HashMap<>();
        params.put(MONTH_TEMPLATE, currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        params.put(YEAR_TEMPLATE, currentDate.getYear());
        params.put(DAYS_TEMPLATE, calendarDays(context, currentDate));
        viewsTemplates.render(context, PLAN, params);
    }

    private List<CalendarDay> calendarDays(Context context, ZonedDateTime currentDate) {
        long id = identity.value(context.req);
        List<Day> plannedDays = days.userFrom(id, currentDate.toEpochSecond());
        int daysNumber = currentDate.getMonth().maxLength();
        List<CalendarDay> calendarDays = new ArrayList<>(daysNumber);
        long currentDay = currentDate.toEpochSecond();
        int plannedDayIdx = 0;
        long monthStart = monthStart(currentDate);
        for (int i = 0; i < daysNumber; i++) {
            long dayStart = monthStart + (DAY_SECONDS * i);
            long dayEnd = dayStart + (DAY_SECONDS - 1);
            long plannedDay = plannedDayIdx < plannedDays.size() ? plannedDays.get(plannedDayIdx).date : -1;
            DayState state = dayState(dayStart, dayEnd, currentDay, plannedDay);
            if (state == DayState.PLANNED) {
                plannedDayIdx++;
            }
            calendarDays.add(new CalendarDay(i + 1, state));
        }
        return calendarDays;
    }

    private long monthStart(ZonedDateTime currentDate) {
        return ZonedDateTime.of(
            currentDate.getYear(), currentDate.getMonthValue(), 1,
            0, 0, 0,
            0, currentDate.getZone()
        ).toEpochSecond();
    }

    private DayState dayState(long dayStart, long dayEnd, long currentDay, long plannedDay) {
        DayState state;
        if (currentDay > dayEnd) {
            state = DayState.NOT_AVAILABLE;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else {
            if (isBetween(dayStart, dayEnd, plannedDay)) {
                state = DayState.PLANNED;
            } else {
                state = DayState.AVAILABLE;
            }
        }
        return state;
    }

    private boolean isBetween(long start, long end, long value) {
        return value >= start && end >= value;
    }
}
