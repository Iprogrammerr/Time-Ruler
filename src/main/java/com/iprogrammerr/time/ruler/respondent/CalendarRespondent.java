package com.iprogrammerr.time.ruler.respondent;

import com.iprogrammerr.time.ruler.model.Identity;
import com.iprogrammerr.time.ruler.model.activity.Dates;
import com.iprogrammerr.time.ruler.model.date.ServerClientDates;
import com.iprogrammerr.time.ruler.model.date.SmartDate;
import com.iprogrammerr.time.ruler.model.date.ZonedDateTimeBuilder;
import com.iprogrammerr.time.ruler.view.rendering.CalendarViews;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarRespondent {

    public static final String PLAN = "plan";
    public static final String HISTORY = "history";
    private static final int MAX_YEAR_OFFSET_VALUE = 100;
    private static final int FIRST_DAY = 1;
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

    public HtmlResponse planPage(HttpServletRequest request, int year, int month) {
        ZonedDateTime currentDate = serverClientDates.zonedClientDate(request);
        int requestedYear = year;
        if (requestedYear < currentDate.getYear()) {
            requestedYear = currentDate.getYear();
        }
        ZonedDateTime requestedDate = new ZonedDateTimeBuilder().withYear(requestedYear)
            .withMonth(month).withDay(currentDate.getDayOfMonth()).build();
        if (requestedDate.getYear() == currentDate.getYear() &&
            currentDate.getMonthValue() > requestedDate.getMonthValue()) {
            requestedDate = requestedDate.withMonth(currentDate.getMonthValue());
        }
        List<Long> days = daysForCalendar(identity.value(request), requestedDate, atMonthEnd(requestedDate),
            serverClientDates.clientUtcOffset(request));
        CalendarViews.Params viewParams = new CalendarViews.Params(true, requestedDate.isAfter(currentDate),
            requestedDate.getYear() < currentDate.getYear() + MAX_YEAR_OFFSET_VALUE,
            currentDate.toEpochSecond(), requestedDate, days
        );
        return new HtmlResponse(views.futureView(viewParams));
    }

    private ZonedDateTime atMonthEnd(ZonedDateTime date) {
        return date.withDayOfMonth(date.toLocalDate().lengthOfMonth());
    }

    private List<Long> daysForCalendar(long userId, ZonedDateTime startDate, ZonedDateTime endDate,
        int clientUtcOffset) {
        long start = new SmartDate(startDate).dayBeginningWithOffset(clientUtcOffset);
        long end = new SmartDate(endDate).dayEndWithOffset(clientUtcOffset);
        List<Long> calendarDates = dates.userPlannedDays(userId, start, end);
        return calendarDates.stream().map(d -> d + clientUtcOffset).collect(Collectors.toList());
    }

    public HtmlResponse historyPage(HttpServletRequest request, int year, int month) {
        long firstDate = dates.userFirstActivity(identity.value(request));
        Instant date = firstDate == 0 ? serverClientDates.clientDate(request) : Instant.ofEpochSecond(firstDate);
        return toPastCalendarPage(request, year, month, serverClientDates.zonedClientDate(request, date));
    }

    private HtmlResponse toPastCalendarPage(HttpServletRequest request, int year, int month,
        ZonedDateTime firstDate) {
        ZonedDateTime currentDate = serverClientDates.zonedClientDate(request);
        int minYear = firstDate.getYear();
        int requestedYear = year;
        if (requestedYear < minYear) {
            requestedYear = currentDate.getYear();
        }
        ZonedDateTime requestedDate = new ZonedDateTimeBuilder().withYear(requestedYear)
            .withMonth(month).withDay(FIRST_DAY).build();
        if (requestedDate.isBefore(firstDate)) {
            requestedDate = firstDate;
        } else if (requestedDate.isAfter(currentDate)) {
            requestedDate = currentDate;
        }
        List<Long> days = daysForCalendar(identity.value(request), requestedDate,
            atMonthEnd(requestedDate), serverClientDates.clientUtcOffset(request));
        CalendarViews.Params params = new CalendarViews.Params(false,
            requestedDate.isAfter(firstDate) && firstDate.getMonthValue() < requestedDate.getMonthValue(),
            isAfterYearMonth(currentDate, requestedDate), currentDate.toEpochSecond(), requestedDate, days);
        return new HtmlResponse(views.pastView(params));
    }

    private boolean isAfterYearMonth(ZonedDateTime first, ZonedDateTime second) {
        return first.getYear() > second.getYear() || (first.getYear() == second.getYear() &&
            first.getMonthValue() > second.getMonthValue());
    }
}
