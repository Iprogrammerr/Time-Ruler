package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Dates;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.YearMonth;
import com.iprogrammerr.time.ruler.view.rendering.CalendarViews;
import io.javalin.Context;
import io.javalin.Javalin;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class CalendarRespondent implements GroupedRespondent {

    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final String PLAN = "plan";
    private static final String HISTORY = "history";
    private final Identity<Long> identity;
    private final CalendarViews views;
    private final Dates dates;
    private final ServerClientDates serverClientDates;

    public CalendarRespondent(Identity<Long> identity, CalendarViews views, Dates dates, ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.dates = dates;
        this.serverClientDates = serverClientDates;
    }

    @Override
    public void init(String group, Javalin app) {
        app.get(group + PLAN, this::showPlan);
        app.get(group + HISTORY, this::showHistory);
    }

    private void showPlan(Context context) {
        renderToFutureCalendar(context);
    }

    //TODO date offset
    private void showHistory(Context context) {
        long firstDate = dates.userFirstActivity(identity.value(context.req));
        Instant date = firstDate == 0 ? Instant.now(Clock.systemUTC()) : Instant.ofEpochSecond(firstDate);
        renderToPastCalendar(context, ZonedDateTime.ofInstant(date, ZoneOffset.UTC));
    }

    private void renderToFutureCalendar(Context context) {
        ZonedDateTime currentDate = ZonedDateTime.now(Clock.systemUTC());
        int currentYear = currentDate.getYear();
        YearMonth yearMonth = new YearMonth(context.queryParamMap(), currentYear + MAX_YEAR_OFFSET_VALUE);
        int requestedYear = yearMonth.year(currentYear);
        if (requestedYear < currentYear) {
            requestedYear = currentYear;
        }
        ZonedDateTime requestedDate = currentDate.withYear(requestedYear)
            .withMonth(yearMonth.month(currentDate.getMonthValue()));
        if (requestedDate.isAfter(currentDate)) {
            requestedDate = requestedDate.withDayOfMonth(1);
        }
        List<Long> days = daysForCalendar(identity.value(context.req), requestedDate, false);
        String view = views.view(true, requestedDate.isAfter(currentDate), currentYear < yearMonth.maxYear,
            requestedDate, days, false);
        context.html(view);
    }

    //TODO offset
    private List<Long> daysForCalendar(long userId, ZonedDateTime requestedDate, boolean fromPast) {
        List<Long> daysForCalendar;
        if (fromPast) {
            daysForCalendar = dates.userPlannedDays(userId, requestedDate.withDayOfMonth(1).toEpochSecond(),
                requestedDate.toEpochSecond());
        } else {
            daysForCalendar = dates.userPlannedDays(userId, requestedDate.toEpochSecond(),
                requestedDate.withDayOfMonth(requestedDate.toLocalDate().lengthOfMonth()).toEpochSecond());
        }
        return daysForCalendar;
    }

    private void renderToPastCalendar(Context context, ZonedDateTime firstDate) {
        ZonedDateTime currentDate = ZonedDateTime.now(Clock.systemUTC());
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        YearMonth yearMonth = new YearMonth(context.queryParamMap(), currentYear);
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
        List<Long> days = daysForCalendar(identity.value(context.req), requestedDate, true);
        String view = views.view(
            false,
            requestedDate.isAfter(firstDate) && firstDate.getMonthValue() < requestedDate.getMonthValue(),
            requestedDate.isBefore(currentDate), requestedDate, days, true
        );
        context.html(view);
    }
}
