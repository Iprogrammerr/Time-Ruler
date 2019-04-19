package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.SmartDate;
import com.iprogrammerr.time.ruler.model.YearMonthDay;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import io.javalin.Context;
import io.javalin.Javalin;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CalendarRespondent implements Respondent {

    private static final long DAY_SECONDS = TimeUnit.DAYS.toSeconds(1);
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final int MAX_MONTH_VALUE = 12;
    private static final String PLAN = "plan";
    private static final String PLAN_TITLE = "Plan";
    private static final String HISTORY = "history";
    private static final String HISTORY_TITLE = "History";
    private static final String CALENDAR = "calendar";
    private static final String YEAR_PARAM = "year";
    private static final String MONTH_PARAM = "month";
    private static final String TITLE_TEMPLATE = "title";
    private static final String PLAN_TEMPLATE = "plan";
    private static final String PREV_TEMPLATE = "prev";
    private static final String NEXT_TEMPLATE = "next";
    private static final String MONTH_TEMPLATE = "month";
    private static final String YEAR_TEMPLATE = "year";
    private static final String DAYS_TEMPLATE = "days";
    private final Identity<Long> identity;
    private final ViewsTemplates viewsTemplates;
    private final Days days;

    public CalendarRespondent(Identity<Long> identity, ViewsTemplates viewsTemplates, Days days) {
        this.identity = identity;
        this.viewsTemplates = viewsTemplates;
        this.days = days;
    }

    @Override
    public void init(Javalin app) {
        app.get(PLAN, this::showPlan);
        app.get(HISTORY, this::showHistory);
    }

    private void showPlan(Context context) {
        if (identity.isValid(context.req)) {
            renderToFutureCalendar(context);
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    private void showHistory(Context context) {
        if (identity.isValid(context.req)) {
            long firstDate = days.userFirstDate(identity.value(context.req));
            Instant dateInstant = firstDate == 0 ? Instant.now() : Instant.ofEpochSecond(firstDate);
            renderToPastCalendar(context, ZonedDateTime.ofInstant(dateInstant, ZoneOffset.UTC));
        } else {
            context.status(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
    }

    //TODO more universal validation mechanism?
    private void renderToFutureCalendar(Context context) {
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneOffset.UTC);
        int currentYear = currentDate.getYear();
        YearMonthDay yearMonth = new YearMonthDay(context.queryParamMap(), currentYear + MAX_YEAR_OFFSET_VALUE);
        int requestedYear = yearMonth.year(currentYear);
        if (requestedYear < currentYear) {
            requestedYear = currentYear;
        }
        ZonedDateTime withOffset = new SmartDate(currentDate)
            .ofYearMonth(requestedYear, yearMonth.month(currentDate.getMonthValue()));
        renderCalendar(
            context, PLAN_TITLE, true,
            withOffset.isAfter(currentDate),
            currentYear < yearMonth.maxYear,
            withOffset.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
            withOffset.getYear(), calendarDays(context, withOffset, false)
        );
    }

    private void renderCalendar(Context context, String title, boolean plan, boolean hasPrevious, boolean hasNext,
        String month, int year, List<CalendarDay> days) {
        Map<String, Object> params = new HashMap<>();
        params.put(TITLE_TEMPLATE, title);
        params.put(PLAN_TEMPLATE, plan);
        params.put(PREV_TEMPLATE, hasPrevious);
        params.put(NEXT_TEMPLATE, hasNext);
        params.put(MONTH_TEMPLATE, month);
        params.put(YEAR_TEMPLATE, year);
        params.put(DAYS_TEMPLATE, days);
        viewsTemplates.render(context, CALENDAR, params);
    }

    private List<CalendarDay> calendarDays(Context context, ZonedDateTime currentDate, boolean fromPast) {
        long id = identity.value(context.req);
        List<Day> plannedDays = fromPast ? days.userTo(id, currentDate.toEpochSecond()) :
            days.userFrom(id, currentDate.toEpochSecond());
        int daysNumber = currentDate.toLocalDate().lengthOfMonth();
        List<CalendarDay> calendarDays = new ArrayList<>(daysNumber);
        int plannedDayIdx = 0;
        long monthStart = monthStart(currentDate);
        for (int i = 0; i < daysNumber; i++) {
            long dayStart = monthStart + (DAY_SECONDS * i);
            long dayEnd = dayStart + (DAY_SECONDS - 1);
            long plannedDay = plannedDayIdx < plannedDays.size() ? plannedDays.get(plannedDayIdx).date : -1;
            DayState state = fromPast ?
                dayStateForPast(dayStart, dayEnd, plannedDay) :
                dayStateForFuture(dayStart, dayEnd, plannedDay);
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

    private DayState dayStateForFuture(long dayStart, long dayEnd, long plannedDay) {
        DayState state;
        long currentDay = Instant.now().getEpochSecond();
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

    private DayState dayStateForPast(long dayStart, long dayEnd, long plannedDay) {
        DayState state;
        long currentDay = Instant.now().getEpochSecond();
        if (dayEnd < currentDay) {
            state = DayState.AVAILABLE;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else {
            if (isBetween(dayStart, dayEnd, plannedDay)) {
                state = DayState.PLANNED;
            } else {
                state = DayState.NOT_AVAILABLE;
            }
        }
        return state;
    }

    private boolean isBetween(long start, long end, long value) {
        return value >= start && end >= value;
    }

    private void renderToPastCalendar(Context context, ZonedDateTime firstDate) {
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneOffset.UTC);
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        YearMonthDay yearMonth = new YearMonthDay(context.queryParamMap(), currentYear);
        int minYear = firstDate.getYear();
        int requestedYear = yearMonth.year(currentYear);
        if (requestedYear < minYear) {
            requestedYear = currentYear;
        }
        int requestedMonth = yearMonth.month(currentMonth);
        ZonedDateTime requestedDate = new SmartDate(currentDate).ofYearMonth(requestedYear, requestedMonth);
        if (requestedDate.isBefore(firstDate)) {
            requestedMonth = firstDate.getMonthValue();
            requestedDate = requestedDate.withMonth(requestedMonth);
        }
        renderCalendar(
            context, HISTORY_TITLE, false,
            requestedDate.isAfter(firstDate),
            requestedDate.isBefore(currentDate),
            requestedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
            requestedDate.getYear(), calendarDays(context, requestedDate, true)
        );
    }
}
