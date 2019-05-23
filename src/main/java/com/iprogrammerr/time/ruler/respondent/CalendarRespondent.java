package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Dates;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.YearMonth;
import com.iprogrammerr.time.ruler.view.rendering.CalendarViews;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

//TODO simplify year month, fix calendar dates
public class CalendarRespondent {

    public static final String PLAN = "plan";
    public static final String HISTORY = "history";
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private final Identity<Long> identity;
    private final CalendarViews views;
    private final Dates dates;
    private final ServerClientDates serverClientDates;

    public CalendarRespondent(Identity<Long> identity, CalendarViews views, Dates dates,
        ServerClientDates serverClientDates) {
        this.identity = identity;
        this.views = views;
        this.dates = dates;
        this.serverClientDates = serverClientDates;
    }

    public HtmlResponse planPage(HttpServletRequest request, Map<String, List<String>> queryParams) {
        ZonedDateTime currentDate = ZonedDateTime.now(Clock.systemUTC());
        int currentYear = currentDate.getYear();
        YearMonth yearMonth = new YearMonth(queryParams, currentDate.getYear() + MAX_YEAR_OFFSET_VALUE);
        int requestedYear = yearMonth.year(currentYear);
        if (requestedYear < currentYear) {
            requestedYear = currentYear;
        }
        ZonedDateTime requestedDate = currentDate.withYear(requestedYear)
            .withMonth(yearMonth.month(currentDate.getMonthValue()));
        if (requestedDate.isAfter(currentDate)) {
            requestedDate = requestedDate.withDayOfMonth(1);
        }
        List<Long> days = daysForCalendar(identity.value(request), requestedDate, false,
            serverClientDates.clientUtcOffset(request));
        String view = views.view(true, requestedDate.isAfter(currentDate), currentYear < yearMonth.maxYear,
            requestedDate, days, false);
        return new HtmlResponse(view);
    }

    private List<Long> daysForCalendar(long userId, ZonedDateTime requestedDate, boolean fromPast,
        int clientUtcOffset) {
        long start;
        long end;
        if (fromPast) {
            start = new SmartDate(requestedDate.withDayOfMonth(1)).dayBeginningWithOffset(clientUtcOffset);
            end = new SmartDate(requestedDate).dayEndWithOffset(clientUtcOffset);
        } else {
            start = new SmartDate(requestedDate).dayBeginningWithOffset(clientUtcOffset);
            end = new SmartDate(requestedDate.withDayOfMonth(requestedDate.toLocalDate().lengthOfMonth()))
                .dayEndWithOffset(clientUtcOffset);
        }
        return dates.userPlannedDays(userId, start, end);
    }

    //TODO date offset
    public HtmlResponse historyPage(HttpServletRequest request, Map<String, List<String>> queryParams) {
        long firstDate = dates.userFirstActivity(identity.value(request));
        Instant date = firstDate == 0 ? Instant.now(Clock.systemUTC()) : Instant.ofEpochSecond(firstDate);
        return toPastCalendarPage(request, queryParams, ZonedDateTime.ofInstant(date, ZoneOffset.UTC));
    }

    private HtmlResponse toPastCalendarPage(HttpServletRequest request, Map<String, List<String>> queryParams,
        ZonedDateTime firstDate) {
        ZonedDateTime currentDate = ZonedDateTime.now(Clock.systemUTC());
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        int minYear = firstDate.getYear();
        YearMonth yearMonth = new YearMonth(queryParams, currentDate.getYear() + MAX_YEAR_OFFSET_VALUE);
        int requestedYear = yearMonth.year(currentYear);
        if (requestedYear < minYear) {
            requestedYear = currentYear;
        }
        int requestedMonth = yearMonth.month(currentMonth);
        ZonedDateTime requestedDate = new SmartDate(currentDate).ofYearMonth(requestedYear, requestedMonth);
        if (requestedDate.isBefore(firstDate)) {
            requestedDate = firstDate;
        } else if (requestedDate.isBefore(currentDate)) {
            requestedDate = requestedDate.withDayOfMonth(requestedDate.toLocalDate().lengthOfMonth());
        }
        List<Long> days = daysForCalendar(identity.value(request), requestedDate, true,
            serverClientDates.clientUtcOffset(request));
        String view = views.view(
            false,
            requestedDate.isAfter(firstDate) && firstDate.getMonthValue() < requestedDate.getMonthValue(),
            requestedDate.isBefore(currentDate), requestedDate, days, true
        );
        return new HtmlResponse(view);
    }
}
