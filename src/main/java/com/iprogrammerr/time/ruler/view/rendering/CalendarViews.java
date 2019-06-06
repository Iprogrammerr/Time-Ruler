package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.model.date.ZonedDateTimeBuilder;
import com.iprogrammerr.time.ruler.model.rendering.ActiveTab;
import com.iprogrammerr.time.ruler.model.rendering.CalendarDay;
import com.iprogrammerr.time.ruler.model.rendering.DayState;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;

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

    public String pastView(Params params) {
        return view(params, true);
    }

    public String futureView(Params params) {
        return view(params, false);
    }

    private String view(Params viewParams, boolean fromPast) {
        Map<String, Object> params = new HashMap<>();
        params.put(PLAN_TEMPLATE, viewParams.plan);
        params.put(ActiveTab.KEY, viewParams.plan ? ActiveTab.PLAN : ActiveTab.HISTORY);
        params.put(PREV_TEMPLATE, viewParams.hasPrevious);
        params.put(NEXT_TEMPLATE, viewParams.hasNext);
        params.put(MONTH_TEMPLATE, viewParams.firstDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US));
        params.put(YEAR_TEMPLATE, viewParams.firstDate.getYear());
        params.put(DAYS_TEMPLATE, calendarDays(viewParams.days, viewParams.firstDate, viewParams.currentDate,
            fromPast));
        return templates.rendered(name, params);
    }

    private List<CalendarDay> calendarDays(List<Long> days, ZonedDateTime firstDate, long currentDate,
        boolean fromPast) {
        int daysNumber = firstDate.toLocalDate().lengthOfMonth();
        List<CalendarDay> calendarDays = new ArrayList<>(daysNumber);
        int plannedDayIdx = 0;
        ZonedDateTime monthStart = monthStart(firstDate);
        for (int i = 0; i < monthStart.getDayOfWeek().getValue() - 1; i++) {
            calendarDays.add(new CalendarDay(0, DayState.EMPTY));
        }
        long monthStartSeconds = monthStart.toEpochSecond();
        long firstDateSeconds = firstDate.toEpochSecond();
        for (int i = 0; i < daysNumber; i++) {
            long dayStart = monthStartSeconds + (DAY_SECONDS * i);
            long dayEnd = dayStart + (DAY_SECONDS - 1);
            long plannedDay = plannedDayIdx < days.size() ? days.get(plannedDayIdx) : -1;
            DayState state = fromPast ?
                dayStateForPast(dayStart, dayEnd, plannedDay, currentDate, firstDateSeconds) :
                dayStateForFuture(dayStart, dayEnd, plannedDay, currentDate);
            if (state == DayState.PLANNED) {
                plannedDayIdx++;
            }
            calendarDays.add(new CalendarDay(i + 1, state));
        }
        return calendarDays;
    }

    private ZonedDateTime monthStart(ZonedDateTime currentDate) {
        return new ZonedDateTimeBuilder()
            .withYear(currentDate.getYear()).withMonth(currentDate.getMonthValue())
            .withDay(1).withHour(0).withSecond(0)
            .build();
    }

    private DayState dayStateForFuture(long dayStart, long dayEnd, long plannedDay, long currentDay) {
        DayState state;
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

    private DayState dayStateForPast(long dayStart, long dayEnd, long plannedDay, long currentDay, long firstDay) {
        DayState state;
        if (isBetween(dayStart, dayEnd, plannedDay)) {
            state = DayState.PLANNED;
        } else if (isBetween(dayStart, dayEnd, currentDay)) {
            state = DayState.CURRENT;
        } else if (dayEnd > firstDay && dayEnd <= currentDay) {
            state = DayState.AVAILABLE;
        } else {
            state = DayState.NOT_AVAILABLE;
        }
        return state;
    }

    private boolean isBetween(long start, long end, long value) {
        return value >= start && end >= value;
    }

    public static class Params {

        public final boolean plan;
        public final boolean hasPrevious;
        public final boolean hasNext;
        public final long currentDate;
        public final ZonedDateTime firstDate;
        public final List<Long> days;

        public Params(boolean plan, boolean hasPrevious, boolean hasNext, long currentDate,
            ZonedDateTime firstDate, List<Long> days) {
            this.plan = plan;
            this.hasPrevious = hasPrevious;
            this.hasNext = hasNext;
            this.currentDate = currentDate;
            this.firstDate = firstDate;
            this.days = days;
        }
    }
}
