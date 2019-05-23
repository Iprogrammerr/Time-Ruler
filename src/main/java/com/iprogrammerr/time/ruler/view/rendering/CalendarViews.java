package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CalendarViews {

    private static final long DAY_SECONDS = TimeUnit.DAYS.toSeconds(1);
    private static final String PLAN_TEMPLATE = "plan";
    private static final String PREV_TEMPLATE = "prev";
    private static final String NEXT_TEMPLATE = "next";
    private static final String MONTH_TEMPLATE = "month";
    private static final String YEAR_TEMPLATE = "year";
    private static final String DAYS_TEMPLATE = "days";
    private final ViewsTemplates templates;
    private final String name;

    public CalendarViews(ViewsTemplates templates, String name) {
        this.templates = templates;
        this.name = name;
    }

    public CalendarViews(ViewsTemplates templates) {
        this(templates, "calendar");
    }

    public String view(boolean plan, boolean hasPrevious, boolean hasNext, ZonedDateTime date, List<Long> days,
        boolean fromPast) {
        Map<String, Object> params = new HashMap<>();
        params.put(PLAN_TEMPLATE, plan);
        params.put(ActiveTab.KEY, plan ? ActiveTab.PLAN : ActiveTab.HISTORY);
        params.put(PREV_TEMPLATE, hasPrevious);
        params.put(NEXT_TEMPLATE, hasNext);
        params.put(MONTH_TEMPLATE, date.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        params.put(YEAR_TEMPLATE, date.getYear());
        params.put(DAYS_TEMPLATE, calendarDays(days, date, fromPast));
        return templates.rendered(name, params);
    }

    private List<CalendarDay> calendarDays(List<Long> days, ZonedDateTime date, boolean fromPast) {
        int daysNumber = date.toLocalDate().lengthOfMonth();
        List<CalendarDay> calendarDays = new ArrayList<>(daysNumber);
        int plannedDayIdx = 0;
        long monthStart = monthStart(date);
        for (int i = 0; i < daysNumber; i++) {
            long dayStart = monthStart + (DAY_SECONDS * i);
            long dayEnd = dayStart + (DAY_SECONDS - 1);
            long plannedDay = plannedDayIdx < days.size() ? days.get(plannedDayIdx) : -1;
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
        if (isBetween(dayStart, dayEnd, plannedDay)) {
            state = DayState.PLANNED;
        } else if (currentDay > dayEnd) {
            state = DayState.NOT_AVAILABLE;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else {
            state = DayState.AVAILABLE;
        }
        return state;
    }

    private DayState dayStateForPast(long dayStart, long dayEnd, long plannedDay) {
        DayState state;
        long currentDay = Instant.now().getEpochSecond();
        if (isBetween(dayStart, dayEnd, plannedDay)) {
            state = DayState.PLANNED;
        } else if (dayEnd < currentDay) {
            state = DayState.AVAILABLE;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else {
            state = DayState.NOT_AVAILABLE;
        }
        return state;
    }

    private boolean isBetween(long start, long end, long value) {
        return value >= start && end >= value;
    }
}
