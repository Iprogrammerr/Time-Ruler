package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.YearMonthDay;
import com.iprogrammerr.time.ruler.model.day.Day;
import com.iprogrammerr.time.ruler.model.day.Days;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.view.rendering.CalendarView;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CalendarRespondent implements GroupedRespondent {

    private static final long DAY_SECONDS = TimeUnit.DAYS.toSeconds(1);
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String PLAN = "plan";
    private static final String HISTORY = "history";
    private final Identity<Long> identity;
    private final CalendarView calendarView;
    private final Days days;

    public CalendarRespondent(Identity<Long> identity, CalendarView calendarView, Days days) {
        this.identity = identity;
        this.calendarView = calendarView;
        this.days = days;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + PLAN, this::showPlan);
        app.get(group + HISTORY, this::showHistory);
    }

    private void showPlan(Context context) {
        renderToFutureCalendar(context);
    }

    private void showHistory(Context context) {
        long firstDate = days.userFirstDate(identity.value(context.req));
        Instant dateInstant = firstDate == 0 ? Instant.now() : Instant.ofEpochSecond(firstDate);
        renderToPastCalendar(context, ZonedDateTime.ofInstant(dateInstant, ZoneOffset.UTC));
    }

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
        context.html(calendarView.rendered(true, withOffset.isAfter(currentDate), currentYear < yearMonth.maxYear,
            withOffset.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
            withOffset.getYear(), calendarDays(context, withOffset, false))
        );
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
        context.html(calendarView.rendered(false,
            requestedDate.isAfter(firstDate) && firstDate.getMonthValue() < requestedDate.getMonthValue(),
            requestedDate.isBefore(currentDate), requestedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US),
            requestedDate.getYear(), calendarDays(context, requestedDate, true)));
    }
}
