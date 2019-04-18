package com.iprogrammerr.time.ruler.respondent.plan;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.SmartDate;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.respondent.Respondent;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.time.ZoneOffset;
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
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final int MAX_MONTH_VALUE = 12;
    private static final String PLAN = "plan";
    private static final String YEAR_PARAM = "year";
    private static final String MONTH_PARAM = "month";
    private static final String PREV_TEMPLATE = "prev";
    private static final String NEXT_TEMPLATE = "next";
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
    //TODO more universal validation mechanism?
    //TODO limit or not next availability?
    private void renderCalendar(Context context) {
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneOffset.UTC);
        int currentYear = currentDate.getYear();
        int maxYear = currentYear + MAX_YEAR_OFFSET_VALUE;
        int requestedYear = context.queryParam(YEAR_PARAM, Integer.class, String.valueOf(currentYear)).get();
        if (requestedYear < currentYear || requestedYear > maxYear) {
            requestedYear = currentYear;
        }
        int currentMonth = currentDate.getMonthValue();
        int requestedMonth = context.queryParam(MONTH_PARAM, Integer.class, String.valueOf(currentMonth)).get();
        if (requestedMonth < 1 || requestedMonth > MAX_MONTH_VALUE) {
            requestedMonth = currentMonth;
        }

        Map<String, Object> params = new HashMap<>();
        params.put(PREV_TEMPLATE, requestedMonth > currentMonth || requestedYear > currentYear);
        params.put(NEXT_TEMPLATE, currentYear < maxYear);
        ZonedDateTime withOffset = new SmartDate(currentDate).ofYearMonth(requestedYear, requestedMonth);
        params.put(MONTH_TEMPLATE, withOffset.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        params.put(YEAR_TEMPLATE, withOffset.getYear());
        boolean hasOffset = requestedYear != currentYear || requestedMonth != currentMonth;
        params.put(DAYS_TEMPLATE, calendarDays(context, withOffset, hasOffset));

        viewsTemplates.render(context, PLAN, params);
    }

    private List<CalendarDay> calendarDays(Context context, ZonedDateTime currentDate, boolean hasOffset) {
        long id = identity.value(context.req);
        List<Day> plannedDays = days.userFrom(id, currentDate.toEpochSecond());
        int daysNumber = currentDate.toLocalDate().lengthOfMonth();
        List<CalendarDay> calendarDays = new ArrayList<>(daysNumber);
        long currentDay = hasOffset ? -1 : currentDate.toEpochSecond();
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
